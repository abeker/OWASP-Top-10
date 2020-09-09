package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.entity.Authority;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.entity.User;
import com.owasp.authenticationservice.repository.IAuthorityRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.security.SecurityEscape;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.ISimpleUserService;
import com.owasp.authenticationservice.util.enums.UserRole;
import com.owasp.authenticationservice.util.enums.UserStatus;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleUserService implements ISimpleUserService {

    private final Logger logger = LoggerFactory.getLogger(SimpleUserService.class);

    private final PasswordEncoder _passwordEncoder;
    private final IAuthorityRepository _authorityRepository;
    private final ISimpleUserRepository _simpleUserRepository;
    private final IUserRepository _userRepository;
    private final TokenUtils _tokenUtils;

    public SimpleUserService(PasswordEncoder passwordEncoder, IAuthorityRepository authorityRepository, ISimpleUserRepository simpleUserRepository, IUserRepository userRepository, TokenUtils tokenUtils) {
        _passwordEncoder = passwordEncoder;
        _authorityRepository = authorityRepository;
        _simpleUserRepository = simpleUserRepository;
        _userRepository = userRepository;
        _tokenUtils = tokenUtils;
    }

    @Override
    public SimpleUserResponse createSimpleUser(CreateSimpleUserRequest request) throws GeneralException {
        if(!request.getPassword().equals(request.getRePassword())){
            logger.warn("[{}] passwords missmatch", request.getUsername());
            throw new GeneralException("Passwords don't match.", HttpStatus.BAD_REQUEST);
        }
        if (isSimpleUserExist(request.getUsername())) {
            logger.warn("[{}] user exists", request.getUsername());
            throw new GeneralException("User already exist.", HttpStatus.BAD_REQUEST);
        }

        sanitizeInputValues(request);
        logger.info("[{}] created registration request", request.getUsername());
        SimpleUser createdSimpleUser = createNewSimpleUser(request);
        SimpleUser savedSimpleUser = _simpleUserRepository.save(createdSimpleUser);

        return mapSimpleUserToSimpleUserResponse(savedSimpleUser);
    }

    private void sanitizeInputValues(CreateSimpleUserRequest request) {
        request.setUsername(SecurityEscape.cleanIt(request.getUsername()));
        request.setPassword(SecurityEscape.cleanIt(request.getPassword()));
        request.setFirstName(SecurityEscape.cleanIt(request.getFirstName()));
        request.setLastName(SecurityEscape.cleanIt(request.getLastName()));
        request.setAddress(SecurityEscape.cleanIt(request.getAddress()));
        request.setSecurityQuestion(SecurityEscape.cleanIt(request.getSecurityQuestion()));
        request.setSsn(SecurityEscape.cleanIt(request.getSsn()));
    }

    @Override
    public List<SimpleUserResponse> getSimpleUserByStatus(String userStatusString) {
        UserStatus userStatus = getUserStatusFromString(userStatusString);

        List<SimpleUser> allSimpleUsers = _simpleUserRepository.findAll()
                .stream()
                .filter(simpleUser -> simpleUser.getUserStatus() == userStatus)
                .collect(Collectors.toList());
        List<SimpleUserResponse> simpleUserResponseList = new ArrayList<>();
        for (SimpleUser simpleUser : allSimpleUsers) {
            simpleUserResponseList.add(mapSimpleUserToSimpleUserResponse(simpleUser));
        }
        return simpleUserResponseList;
    }

    @Override
    public SimpleUserResponse getSimpleUser(UUID id) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        return mapSimpleUserToSimpleUserResponse(simpleUser);
    }

    @Override
    public SimpleUserResponse getSimpleUserFromToken(String token) {
        String simpleUserUsername = _tokenUtils.getUsernameFromToken(token);
        User user = _userRepository.findOneByUsername(simpleUserUsername);

        return getSimpleUser(user.getId());
    }

    @Override
    public void addRolesAfterPay(UUID userId) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(userId);
        if(simpleUser != null) {
            logger.info("[{}] add roles after pay", "admin");
            Set<Authority> authorities = simpleUser.getRoles();
            authorities.add(_authorityRepository.findByName("ROLE_COMMENT_USER"));
            authorities.add(_authorityRepository.findByName("ROLE_REVIEWER_USER"));
            _simpleUserRepository.save(simpleUser);
        }
    }

    private UserStatus getUserStatusFromString(String userStatusString) {
        UserStatus userStatus;
        switch (userStatusString) {
            case "PENDING": userStatus = UserStatus.PENDING;
            break;
            case "APPROVED": userStatus = UserStatus.APPROVED;
            break;
            case "DENIED": userStatus = UserStatus.DENIED;
            break;
            default: userStatus = null;
        }

        return userStatus;
    }

    private boolean isSimpleUserExist(String username) {
        return _userRepository.findOneByUsername(username) != null;
    }

    private SimpleUser createNewSimpleUser(CreateSimpleUserRequest request) {
        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setUsername(request.getUsername());
        simpleUser.setPassword(_passwordEncoder.encode(request.getPassword()));
        simpleUser.setUserStatus(UserStatus.PENDING);
        simpleUser.setUserRole(UserRole.SIMPLE_USER);
        simpleUser.setAddress(request.getAddress());
        simpleUser.setFirstName(request.getFirstName());
        simpleUser.setLastName(request.getLastName());
        simpleUser.setSsn(request.getSsn());
        simpleUser.setSecurityQuestion(request.getSecurityQuestion());
        addAuthoritiesSimpleUser(simpleUser);

        return simpleUser;
    }

    private SimpleUser unsafeCreateNewSimpleUser(CreateSimpleUserRequest request) {
        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setUsername(request.getUsername());
        simpleUser.setPassword(request.getPassword());
        simpleUser.setUserStatus(UserStatus.PENDING);
        simpleUser.setUserRole(UserRole.SIMPLE_USER);
        simpleUser.setAddress(request.getAddress());
        simpleUser.setFirstName(request.getFirstName());
        simpleUser.setLastName(request.getLastName());
        simpleUser.setSsn(request.getSsn());
        addAuthoritiesSimpleUser(simpleUser);

        return simpleUser;
    }

    private SimpleUserResponse mapSimpleUserToSimpleUserResponse(SimpleUser savedSimpleUser) {
        SimpleUserResponse simpleUserResponse = new SimpleUserResponse();
        simpleUserResponse.setId(savedSimpleUser.getId());
        simpleUserResponse.setFirstName(savedSimpleUser.getFirstName());
        simpleUserResponse.setLastName(savedSimpleUser.getLastName());
        simpleUserResponse.setUsername(savedSimpleUser.getUsername());
        simpleUserResponse.setUserRole(savedSimpleUser.getUserRole().toString());
        return simpleUserResponse;
    }

    private void addAuthoritiesSimpleUser(SimpleUser user) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(_authorityRepository.findByName("ROLE_SIMPLE_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_RENT_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_REQUEST"));
        user.setAuthorities(new HashSet<>(authorities));
    }

}
