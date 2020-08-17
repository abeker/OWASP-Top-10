package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.entity.Authority;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.repository.IAuthorityRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.services.ISimpleUserService;
import com.owasp.authenticationservice.util.enums.UserRole;
import com.owasp.authenticationservice.util.enums.UserStatus;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleUserService implements ISimpleUserService {

    private final PasswordEncoder _passwordEncoder;
    private final IAuthorityRepository _authorityRepository;
    private final ISimpleUserRepository _simpleUserRepository;
    private final IUserRepository _userRepository;

    public SimpleUserService(PasswordEncoder passwordEncoder, IAuthorityRepository authorityRepository, ISimpleUserRepository simpleUserRepository, IUserRepository userRepository) {
        _passwordEncoder = passwordEncoder;
        _authorityRepository = authorityRepository;
        _simpleUserRepository = simpleUserRepository;
        _userRepository = userRepository;
    }

    @Override
    public SimpleUserResponse createSimpleUser(CreateSimpleUserRequest request) throws GeneralException {
        if(!request.getPassword().equals(request.getRePassword())){
            throw new GeneralException("Passwords don't match.", HttpStatus.BAD_REQUEST);
        }
        if (isSimpleUserExist(request.getUsername())) {
            throw new GeneralException("User already exist.", HttpStatus.BAD_REQUEST);
        }
        SimpleUser createdSimpleUser = createNewSimpleUser(request);
        SimpleUser savedSimpleUser = _simpleUserRepository.save(createdSimpleUser);

        return mapSimpleUserToSimpleUserResponse(savedSimpleUser);
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
        authorities.add(_authorityRepository.findByName("ROLE_REQUEST"));       // treba dda se dodaje kada se rentira
//        authorities.add(_authorityRepository.findByName("ROLE_COMMENT_USER"));  // treba da se dodaje kada se rentira
//        authorities.add(_authorityRepository.findByName("ROLE_MESSAGE_USER"));  // treba da se dodaje kada se rentira
//        authorities.add(_authorityRepository.findByName("ROLE_REVIEWER_USER")); // treba da se dodaje kada se rentira
        user.setAuthorities(new HashSet<>(authorities));
    }

}
