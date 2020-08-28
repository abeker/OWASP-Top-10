package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.LoginCredentialsRequest;
import com.owasp.authenticationservice.dto.response.UserResponse;
import com.owasp.authenticationservice.dto.response.UserResponseBuilder;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.entity.User;
import com.owasp.authenticationservice.entity.UserDetailsImpl;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.IAuthService;
import com.owasp.authenticationservice.util.enums.UserRole;
import com.owasp.authenticationservice.util.enums.UserStatus;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager _authenticationManager;
    private final TokenUtils _tokenUtils;
    private final PasswordEncoder _passwordEncoder;
    private final IUserRepository _userRepository;
    private final DataSource _dataSource;
    private final EntityManager _em;

    public AuthService(AuthenticationManager authenticationManager, TokenUtils tokenUtils, PasswordEncoder passwordEncoder, IUserRepository userRepository, DataSource dataSource, EntityManager em) {
        _authenticationManager = authenticationManager;
        _tokenUtils = tokenUtils;
        _passwordEncoder = passwordEncoder;
        _userRepository = userRepository;
        _dataSource = dataSource;
        _em = em;
    }

    @Override
    public UserResponse login(LoginCredentialsRequest request, HttpServletRequest httpServletRequest) throws GeneralException, SQLException {
        if(request.isSQLI()) {
            return unsafeLogin(request, httpServletRequest);
        }
        User user = _userRepository.findOneByUsername(request.getUsername());

        if(!isUserFound(user, request)) {
            throw new GeneralException("Bad credentials.", HttpStatus.BAD_REQUEST);
        }

        checkSimpleUserStatus(user);
        Authentication authentication = loginSimpleUser(request.getUsername(), request.getPassword());
        return createLoginUserResponse(authentication, user);
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
