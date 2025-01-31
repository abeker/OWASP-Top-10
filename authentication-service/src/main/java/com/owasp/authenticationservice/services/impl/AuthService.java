package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.BrowserFingerprintRequest;
import com.owasp.authenticationservice.dto.request.ChangePasswordRequest;
import com.owasp.authenticationservice.dto.request.LoginCredentialsRequest;
import com.owasp.authenticationservice.dto.response.UserQuestionResponse;
import com.owasp.authenticationservice.dto.response.UserResponse;
import com.owasp.authenticationservice.dto.response.UserResponseBuilder;
import com.owasp.authenticationservice.entity.*;
import com.owasp.authenticationservice.repository.IBrowserFingerPrintRepository;
import com.owasp.authenticationservice.repository.ILoginAttemptRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.IAuthService;
import com.owasp.authenticationservice.services.IEmailService;
import com.owasp.authenticationservice.util.enums.UserRole;
import com.owasp.authenticationservice.util.enums.UserStatus;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"ConstantConditions", "unused"})
@Service
public class AuthService implements IAuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    public static final String ERROR_CODE = "ERRONEOUS_SPECIAL_CHARS";

    private final AuthenticationManager _authenticationManager;
    private final TokenUtils _tokenUtils;
    private final PasswordEncoder _passwordEncoder;
    private final IUserRepository _userRepository;
    private final DataSource _dataSource;
    private final EntityManager _em;
    private final ILoginAttemptRepository _loginAttemptRepository;
    private final IBrowserFingerPrintRepository _browserFingerPrintRepository;
    private final ISimpleUserRepository _simpleUserRepository;
    private final IEmailService _emailService;

    public AuthService(AuthenticationManager authenticationManager, TokenUtils tokenUtils, PasswordEncoder passwordEncoder, IUserRepository userRepository, DataSource dataSource, EntityManager em, ILoginAttemptRepository loginAttemptRepository, IBrowserFingerPrintRepository browserFingerPrintRepository, ISimpleUserRepository simpleUserRepository, IEmailService emailService) {
        _authenticationManager = authenticationManager;
        _tokenUtils = tokenUtils;
        _passwordEncoder = passwordEncoder;
        _userRepository = userRepository;
        _dataSource = dataSource;
        _em = em;
        _loginAttemptRepository = loginAttemptRepository;
        _browserFingerPrintRepository = browserFingerPrintRepository;
        _simpleUserRepository = simpleUserRepository;
        _emailService = emailService;
    }

    @Override
    public UserResponse login(LoginCredentialsRequest request, HttpServletRequest httpServletRequest) throws GeneralException {
        if(request.isSQLI()) {
            logger.error("[{}] SQLI attack", request.getUsername());
            return unsafeLogin(request);
        } else if(request.isDictionaryAttack()) {
//            logger.error("[{}] dictionary attack", request.getUsername());
            return unsafeDictionaryLogin(request);
        }
        User user = _userRepository.findOneByUsername(request.getUsername());
        checkLoginAttempts(request, httpServletRequest, user);

        if(!isUserFound(user, request)) {
            logger.warn("[{}] bad credentials", request.getUsername());
            throw new GeneralException("Bad credentials.", HttpStatus.BAD_REQUEST);
        }

        checkSimpleUserStatus(user);
        Authentication authentication = loginSimpleUser(request.getUsername(), request.getPassword());
        return createLoginUserResponse(authentication, user);
    }

    private void checkLoginAttempts(LoginCredentialsRequest request, HttpServletRequest httpServletRequest, User user) {
        BrowserFingerprint browserFingerprint = createBrowserFingerPrint(request.getBrowserFingerprint(), httpServletRequest);
        LoginAttempt loginAttempt = getLoginAttemptFromBrowserFingerprint(browserFingerprint);
        changeLoginAttempts(isUserFound(user, request), loginAttempt, browserFingerprint, request);
    }

    private void changeLoginAttempts(boolean isUserFound, LoginAttempt loginAttempt, BrowserFingerprint browserFingerprint, LoginCredentialsRequest request) {
        if(isUserFound) {
            if(loginAttempt != null) {
                logger.info("[{}] reset login attempts" + request.getUsername());
                loginAttempt.setAttempts(0);
                _loginAttemptRepository.save(loginAttempt);
            }
        } else {
            if(loginAttempt != null) {
                checkNumberOfAttempts(loginAttempt, request);
                loginAttempt.setAttempts(loginAttempt.getAttempts() + 1);
                logger.info("[{}] unsuccessfull login attempt({})", request.getUsername(), loginAttempt.getAttempts());
                _loginAttemptRepository.save(loginAttempt);
            } else {
                BrowserFingerprint browserFingerprintSaved = _browserFingerPrintRepository.save(browserFingerprint);
                LoginAttempt loginAttemptSave = new LoginAttempt();
                loginAttemptSave.setBrowserFingerprint(browserFingerprintSaved);
                logger.info("[{}] unsuccessfull login attempt(1)" + request.getUsername());
                _loginAttemptRepository.save(loginAttemptSave);
            }
        }
    }

    private void checkNumberOfAttempts(LoginAttempt loginAttempt, LoginCredentialsRequest request) {
        if(loginAttempt.getAttempts() >= 4) {
            if(loginAttempt.getTimeFirstMistake().isBefore(LocalDateTime.now().minusHours(3600000))) {  // ban na sat vremena prosao
                if (request != null) {
                    logger.info("[{}] reset login attempts" + request.getUsername());
                }
                loginAttempt.setAttempts(0);
                _loginAttemptRepository.save(loginAttempt);
            } else {
                if (request != null) {
                    logger.error("[{}] overhead login attempts", request.getUsername());
                }
                throw new GeneralException("Login attempts is more than 5.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private LoginAttempt getLoginAttemptFromBrowserFingerprint(BrowserFingerprint browserFingerprint) {
        List<LoginAttempt> allLoginAttempts = _loginAttemptRepository.findAll();
        for (LoginAttempt loginAttempt : allLoginAttempts) {
            if(isBrowserFingerprintEqual(loginAttempt.getBrowserFingerprint(), browserFingerprint)) {
                return loginAttempt;
            }
        }

        return null;
    }

    private boolean isBrowserFingerprintEqual(BrowserFingerprint browserFingerprint, BrowserFingerprint browserFingerprintRequest) {
        return browserFingerprint.getAddress().equalsIgnoreCase(browserFingerprintRequest.getAddress())
                && browserFingerprint.getBrowserName().equalsIgnoreCase(browserFingerprintRequest.getBrowserName())
                && browserFingerprint.getBrowserVersion().equalsIgnoreCase(browserFingerprintRequest.getBrowserVersion())
                && browserFingerprint.getCPU().equalsIgnoreCase(browserFingerprintRequest.getCPU())
                && browserFingerprint.getFingerprint().equalsIgnoreCase(browserFingerprintRequest.getFingerprint())
                && browserFingerprint.getLanguage().equalsIgnoreCase(browserFingerprintRequest.getLanguage())
                && browserFingerprint.getOS().equalsIgnoreCase(browserFingerprintRequest.getOS())
                && browserFingerprint.getOSVersion().equalsIgnoreCase(browserFingerprintRequest.getOSVersion())
                && browserFingerprint.getPlugins().equalsIgnoreCase(browserFingerprintRequest.getPlugins())
                && browserFingerprint.getScreenPrint().equalsIgnoreCase(browserFingerprintRequest.getScreenPrint())
                && browserFingerprint.getTimeZone().equalsIgnoreCase(browserFingerprintRequest.getTimeZone())
                && browserFingerprint.getUser_agent().equalsIgnoreCase(browserFingerprintRequest.getUser_agent());
    }

    private BrowserFingerprint createBrowserFingerPrint(BrowserFingerprintRequest browserFingerprintRequest, HttpServletRequest httpServletRequest) {
        String user_agent = httpServletRequest.getHeader("User-Agent");
        String host = httpServletRequest.getHeader("Host");
        BrowserFingerprint browserFingerprint = new BrowserFingerprint();
        browserFingerprint.setAddress(host);
        browserFingerprint.setUser_agent(user_agent);
        mapBrowserFingerprintDTOToBrowserFingerprint(browserFingerprint, browserFingerprintRequest);

        return browserFingerprint;
    }

    private void mapBrowserFingerprintDTOToBrowserFingerprint(BrowserFingerprint browserFingerprintForSet, BrowserFingerprintRequest browserFingerprintRequest) {
        browserFingerprintForSet.setBrowserVersion(browserFingerprintRequest.getBrowserVersion());
        browserFingerprintForSet.setBrowserName(browserFingerprintRequest.getBrowserName());
        browserFingerprintForSet.setCPU(browserFingerprintRequest.getCpu());
        browserFingerprintForSet.setFingerprint(browserFingerprintRequest.getFingerprint());
        browserFingerprintForSet.setLanguage(browserFingerprintRequest.getLanguage());
        browserFingerprintForSet.setOS(browserFingerprintRequest.getOs());
        browserFingerprintForSet.setOSVersion(browserFingerprintRequest.getOsVersion());
        browserFingerprintForSet.setPlugins(browserFingerprintRequest.getPlugins());
        browserFingerprintForSet.setScreenPrint(browserFingerprintRequest.getScreenPrint());
        browserFingerprintForSet.setTimeZone(browserFingerprintRequest.getTimeZone());
    }

    private UserResponse unsafeDictionaryLogin(LoginCredentialsRequest request) {
        User user = _userRepository.findOneByUsername(request.getUsername());
        if(!isUserFound(user, request)) {
            logger.warn("[{}] bad credentials", request.getUsername());
            throw new GeneralException("Bad credentials.", HttpStatus.BAD_REQUEST);
        }

        checkSimpleUserStatus(user);
        Authentication authentication = loginSimpleUser(request.getUsername(), request.getPassword());
        return createLoginUserResponse(authentication, user);
    }

    private UserResponse unsafeLogin(LoginCredentialsRequest request) throws GeneralException {
        UserResponseBuilder user = unsafeFindAccountsByUsernameAndPassword(request.getUsername(), request.getPassword());
        List<UserResponseBuilder> userList2 = unsafeJpaFindAccountsByUsername(request.getUsername());

        if(user != null) {
            return new UserResponse(user.getId(), user.getUsername(),
                    "fakeToken", user.getUserRole(), 60000);
        } else {
            throw new GeneralException("unsucessful login", HttpStatus.BAD_REQUEST);
        }
    }

    private UserResponseBuilder unsafeFindAccountsByUsernameAndPassword(String username, String password) {
        String sql = "select * from user_entity where username = '" + username + "' and password = '"+ password + "';";
        try (Connection c = _dataSource.getConnection();
            ResultSet rs = c.createStatement().executeQuery(sql)) {
            if (rs.next()) {
                return UserResponseBuilder.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .userRole(rs.getString("user_role"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .build();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public List<UserResponseBuilder> unsafeJpaFindAccountsByUsername(String username) {
        String jql = "from user_entity where username = '" + username + "'";
        TypedQuery<User> q = _em.createQuery(jql, User.class);
        return q.getResultList()
                .stream()
                .map(user -> UserResponseBuilder.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .userRole(user.getUserRole().toString())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public String getPermission(String token) {
        String username = _tokenUtils.getUsernameFromToken(token);
        User user = _userRepository.findOneByUsername(username);
        StringBuilder retVal = new StringBuilder();
        for (GrantedAuthority authority : user.getAuthorities()) {
            retVal.append(authority.getAuthority()).append(",");
        }
        return retVal.substring(0,retVal.length()-1);
    }

    @Override
    public UserResponse getUser(UUID userId) {
        User user = _userRepository.findOneById(userId);
        throwExceptionIfUserNull(user);
        return mapUserToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String userEmail) {
        User user = _userRepository.findOneByUsername(userEmail);
        throwExceptionIfUserNull(user);
        return mapUserToUserResponse(user);
    }

    @Override
    public UserQuestionResponse getUserQuestionByEmail(String userEmail) {
        User user = _userRepository.findOneByUsername(userEmail);
        throwExceptionIfUserNull(user);
        SimpleUser simpleUser = _simpleUserRepository.findOneById(user.getId());
        throwExceptionIfNotSimpleUser(simpleUser);
        return new UserQuestionResponse(simpleUser.getSecurityQuestion().getQuestion());
    }

    @Override
    public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        } else {
            throw new GeneralException("Fail logout", HttpStatus.BAD_REQUEST);
        }
    }

    private void throwExceptionIfNotSimpleUser(SimpleUser simpleUser) {
        if(simpleUser == null) {
            throw new GeneralException("This is not simple user", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public boolean checkPassword(String userPassword) throws IOException {
        String filePath = new File("").getAbsolutePath();
        File file = new File( filePath + "/authentication-service/weak_passwords.txt");

        return isPasswordWeak(userPassword, file);
    }

    @Override
    public boolean canAgainLogin(BrowserFingerprintRequest browserFingerprint, HttpServletRequest httpServletRequest) {
        BrowserFingerprint browserFingerprintCreated = createBrowserFingerPrint(browserFingerprint, httpServletRequest);
        LoginAttempt loginAttempt = getLoginAttemptFromBrowserFingerprint(browserFingerprintCreated);

        if(loginAttempt == null) {
            return true;
        }
        try {
            checkNumberOfAttempts(loginAttempt, null);
        } catch (GeneralException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkSecurityQuestion(String token, String answer) {
        String username = _tokenUtils.getUsernameFromToken(token);
        User user = _userRepository.findOneByUsername(username);
        SimpleUser simpleUser = _simpleUserRepository.findOneById(user.getId());
        if(!simpleUser.getSecurityQuestion().equals(answer)) {
            logger.warn("[{}] security question incorrect", username);
            return false;
        }
        return true;
    }

    @Override
    public boolean changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = _userRepository.findOneByUsername(changePasswordRequest.getUsername());
        if(user == null) {
            logger.warn("user [{}] not found", changePasswordRequest.getUsername());
            return false;
        }

        SimpleUser simpleUser = _simpleUserRepository.findOneById(user.getId());
        if(simpleUser != null) {
            if(!_passwordEncoder.matches(changePasswordRequest.getSecurityQuestion(), simpleUser.getSecurityQuestion().getAnswer())) {
                logger.warn("[{}] security question incorrect", user.getUsername());
                return false;
            }
        } else {
            return false;
        }

        logger.info("[{}] changed password", user.getUsername());
        String newPassword = generateStrongNewPassword();
        simpleUser.setPassword(_passwordEncoder.encode(newPassword));
        _simpleUserRepository.save(simpleUser);
        _emailService.newPasswordAnnouncementMail(simpleUser, newPassword);

        return true;
    }

    private String generateStrongNewPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }

    @Override
    public boolean isPasswordWeak(String theWord, File theFile) throws FileNotFoundException {
        return (new Scanner(theFile).useDelimiter("\\Z").next()).contains(theWord);
    }

    private void throwExceptionIfUserNull(User user) throws GeneralException {
        if(user == null) {
            throw new GeneralException("This user doesn't exist.", HttpStatus.BAD_REQUEST);
        }
    }

    private UserResponse createLoginUserResponse(Authentication authentication, User user) {
        UserDetailsImpl userLog = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = _tokenUtils.generateToken(userLog.getUsername());
        int expiresIn = _tokenUtils.getExpiredIn();

        UserResponse userResponse = mapUserToUserResponse(user);
        userResponse.setToken(jwt);
        userResponse.setTokenExpiresIn(expiresIn);

        return userResponse;
    }

    private Authentication loginSimpleUser(String mail, String password) {
        Authentication authentication = null;
        try {
            authentication = _authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(mail, password));
        }catch (BadCredentialsException e){
            logger.warn("[{}] registration pending", mail);
            throw new GeneralException("Bad credentials.", HttpStatus.BAD_REQUEST);
        }catch (DisabledException e){
            logger.warn("[{}] bad credentials", mail);
            throw new GeneralException("Your registration request hasn't been approved yet.", HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        return authentication;
    }

    private void checkSimpleUserStatus(User user) {
        if(user.getUserRole() == UserRole.SIMPLE_USER){
            if( ((SimpleUser)user).getUserStatus().equals(UserStatus.PENDING) ) {
                logger.warn("[{}] registration pending", user.getUsername());
                throw new GeneralException("Your registration hasn't been approved yet.", HttpStatus.BAD_REQUEST);
            }
            if( ((SimpleUser)user).getUserStatus().equals(UserStatus.DENIED) ) {
                logger.warn("[{}] registration denied", user.getUsername());
                throw new GeneralException("Your registration has been denied.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean isUserFound(User user, LoginCredentialsRequest request) {
        return user != null && _passwordEncoder.matches(request.getPassword(), user.getPassword());
    }

    private UserResponse mapUserToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());

        userResponse.setUsername(user.getUsername());
        userResponse.setUserRole(user.getUserRole().toString());
        return userResponse;
    }
}
