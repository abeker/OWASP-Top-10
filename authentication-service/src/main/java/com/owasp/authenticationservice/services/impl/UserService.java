package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.response.UserInfoResponse;
import com.owasp.authenticationservice.entity.Agent;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.entity.User;
import com.owasp.authenticationservice.repository.IAgentRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.IUserService;
import com.owasp.authenticationservice.util.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final IUserRepository _userRepository;
    private final IAgentRepository _agentRepository;
    private final ISimpleUserRepository _simpleUserRepository;
    private final TokenUtils _tokenUtils;

    public UserService(IUserRepository userRepository, IAgentRepository agentRepository, ISimpleUserRepository simpleUserRepository, TokenUtils tokenUtils) {
        _userRepository = userRepository;
        _agentRepository = agentRepository;
        _simpleUserRepository = simpleUserRepository;
        _tokenUtils = tokenUtils;
    }

    @Override
    public UserInfoResponse getUserInfo(String username) {
        logger.info("[{}] retrieve user-info", username);
        User user = _userRepository.findOneByUsername(username);
        if(user != null) {
            return mapUserToUserInfoResponse(user);
        }

        return null;
    }

    @Override
    public User getUserFromToken(String token) {
        String username = _tokenUtils.getUsernameFromToken(token);
        return _userRepository.findOneByUsername(username);
    }

    @Override
    public String getCurrentUser(String token) {
        User currentUser = getUserFromToken(token);
        return currentUser.getUsername();
    }

    private UserInfoResponse mapUserToUserInfoResponse(User user) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUsername(user.getUsername());
        userInfoResponse.setFirstName(user.getFirstName());
        userInfoResponse.setLastName(user.getLastName());
        userInfoResponse.setUserRole(user.getUserRole().toString());
        if(user.getUserRole().equals(UserRole.AGENT)) {
            Agent agent = _agentRepository.findOneById(user.getId());
            userInfoResponse.setAddress(agent.getAddress());
        } else if(user.getUserRole().equals(UserRole.SIMPLE_USER)) {
            SimpleUser simpleUser = _simpleUserRepository.findOneById(user.getId());
            userInfoResponse.setAddress(simpleUser.getAddress());
            userInfoResponse.setSecurityQuestion(simpleUser.getSecurityQuestion().getQuestion());
            userInfoResponse.setSsn(simpleUser.getSsn());
        }

        return userInfoResponse;
    }
}
