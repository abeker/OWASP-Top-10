package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.BrowserFingerprintRequest;
import com.owasp.authenticationservice.dto.request.ChangePasswordRequest;
import com.owasp.authenticationservice.dto.request.LoginCredentialsRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
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

@SuppressWarnings("ConstantConditions")
@Service
public class AuthService implements IAuthService {

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
    public UserResponse login(LoginCredentialsRequest request, HttpServletRequest httpServletRequest) throws GeneralException, SQLException {
        if(request.isSQLI()) {
            return unsafeLogin(request, httpServletRequest);
        }
        User user = _userRepository.findOneByUsername(request.getUsername());
        checkLoginAttempts(request, httpServletRequest, user);

        if(!isUserFound(user, request)) {
            throw new GeneralException("Bad credentials.", HttpStatus.BAD_REQUEST);
        }

        checkSimpleUserStatus(user);
        Authentication authentication = loginSimpleUser(request.getUsername(), request.getPassword());
        return createLoginUserResponse(authentication, user);
    }

    private void checkLoginAttempts(LoginCredentialsRequest request, HttpServletRequest httpServletRequest, User user) {
        BrowserFingerprint browserFingerprint = createBrowserFingerPrint(request.getBrowserFingerprint(), httpServletRequest);
        LoginAttempt loginAttempt = getLoginAttemptFromBrowserFingerprint(browserFingerprint);
        changeLoginAttempts(isUserFound(user, request), loginAttempt, browserFingerprint);
    }

    private void changeLoginAttempts(boolean isUserFound, LoginAttempt loginAttempt, BrowserFingerprint browserFingerprint) {
        if(isUserFound) {
            if(loginAttempt != null) {
                loginAttempt.setAttempts(0);
                _loginAttemptRepository.save(loginAttempt);
            }
        } else {
            if(loginAttempt != null) {
                checkNumberOfAttempts(loginAttempt);
                loginAttempt.setAttempts(loginAttempt.getAttempts() + 1);
                _loginAttemptRepository.save(loginAttempt);
            } else {
                BrowserFingerprint browserFingerprintSaved = _browserFingerPrintRepository.save(browserFingerprint);
                LoginAttempt loginAttemptSave = new LoginAttempt();
                loginAttemptSave.setBrowserFingerprint(browserFingerprintSaved);
                _loginAttemptRepository.save(loginAttemptSave);
            }
        }
    }

    private void checkNumberOfAttempts(LoginAttempt loginAttempt) {
        if(loginAttempt.getAttempts() >= 4) {
            if(loginAttempt.getTimeFirstMistake().isBefore(LocalDateTime.now().minusHours(3600000))) {  // ban na sat vremena prosao
                loginAttempt.setAttempts(0);
                _loginAttemptRepository.save(loginAttempt);
            } else {
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

    private UserResponse unsafeLogin(LoginCredentialsRequest request, HttpServletRequest httpServletRequest) throws SQLException {
        UserResponseBuilder user = unsafeFindAccountsByUsername(request.getUsername());
        List<UserResponseBuilder> userList2 = unsafeJpaFindAccountsByUsername(request.getUsername());

        return new UserResponse(user.getId(), user.getUsername(),
                "fakeToken", user.getUserRole(), 60000);
    }

    private UserResponseBuilder unsafeFindAccountsByUsername(String username) throws SQLException {
        String sql = "select * from user_entity where username = '" + username + "'";
        try (Connection c = _dataSource.getConnection();
            ResultSet rs = c.createStatement().executeQuery(sql)) {
            if (rs.next()) {
                UserResponseBuilder user = UserResponseBuilder.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .userRole(rs.getString("user_role"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .build();
                return user;
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
        String retVal = "";
        for (GrantedAuthority authority : user.getAuthorities()) {
            retVal += authority.getAuthority()+",";
        }
        return retVal.substring(0,retVal.length()-1);
    }

    @Override
    public UserResponse getUser(UUID userId) {
        User user = _userRepository.findOneById(userId);
        throwErrorIfUserNull(user);
        return mapUserToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String userEmail) {
        User user = _userRepository.findOneByUsername(userEmail);
        throwErrorIfUserNull(user);
        return mapUserToUserResponse(user);
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
            checkNumberOfAttempts(loginAttempt);
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
        if(simpleUser.getSecurityQuestion().equals(answer)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = _userRepository.findOneByUsername(changePasswordRequest.getUsername());
        if(user == null) {
            return false;
        }
        SimpleUser simpleUser = _simpleUserRepository.findOneById(user.getId());
        if(simpleUser != null) {
            if(!simpleUser.getSecurityQuestion().equals(changePasswordRequest.getSecurityQuestion())) {
                return false;
            }
        } else {
            return false;
        }

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

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }

    public boolean isPasswordWeak(String theWord, File theFile) throws FileNotFoundException {
        return (new Scanner(theFile).useDelimiter("\\Z").next()).contains(theWord);
    }

    private void throwErrorIfUserNull(User user) throws GeneralException {
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
            throw new GeneralException("Bad credentials.", HttpStatus.BAD_REQUEST);
        }catch (DisabledException e){
            throw new GeneralException("Your registration request hasn't been approved yet.", HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    private void checkSimpleUserStatus(User user) {
        if(user.getUserRole() == UserRole.SIMPLE_USER){
            if( ((SimpleUser)user).getUserStatus().equals(UserStatus.PENDING) ) {
                throw new GeneralException("Your registration hasn't been approved yet.", HttpStatus.BAD_REQUEST);
            }
            if( ((SimpleUser)user).getUserStatus().equals(UserStatus.DENIED) ) {
                throw new GeneralException("Your registration has been denied.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private boolean isUserFound(User user, LoginCredentialsRequest request) {
        if(user == null || !_passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return false;
        }
        return true;
    }

    private UserResponse mapUserToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());

        userResponse.setUsername(user.getUsername());
        userResponse.setUserRole(user.getUserRole().toString());
        return userResponse;
    }
}
