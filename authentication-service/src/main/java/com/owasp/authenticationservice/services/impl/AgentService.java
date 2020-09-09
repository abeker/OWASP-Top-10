package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.CreateAgentRequest;
import com.owasp.authenticationservice.dto.response.AgentResponse;
import com.owasp.authenticationservice.entity.Agent;
import com.owasp.authenticationservice.entity.Authority;
import com.owasp.authenticationservice.entity.User;
import com.owasp.authenticationservice.repository.IAgentRepository;
import com.owasp.authenticationservice.repository.IAuthorityRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.security.TokenUtils;
import com.owasp.authenticationservice.services.IAgentService;
import com.owasp.authenticationservice.util.enums.UserRole;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class AgentService implements IAgentService {

    private final Logger logger = LoggerFactory.getLogger(AgentService.class);

    private final IAgentRepository _agentRepository;
    private final IUserRepository _userRepository;
    private final PasswordEncoder _passwordEncoder;
    private final IAuthorityRepository _authorityRepository;
    private final UserService _userService;
    private final TokenUtils _tokenUtils;

    public AgentService(IAgentRepository agentRepository, IUserRepository userRepository, PasswordEncoder passwordEncoder, IAuthorityRepository authorityRepository, UserService userService, TokenUtils tokenUtils) {
        _agentRepository = agentRepository;
        _userRepository = userRepository;
        _passwordEncoder = passwordEncoder;
        _authorityRepository = authorityRepository;
        _userService = userService;
        _tokenUtils = tokenUtils;
    }

    @Override
    public AgentResponse createAgent(CreateAgentRequest request, String token) {
        if(!request.getPassword().equals(request.getRePassword())){
            logger.warn("[{}] passwords missmatch", _userService.getCurrentUser(token));
            throw new GeneralException("Passwords don't match.", HttpStatus.BAD_REQUEST);
        }
        if(isAgentExist(request.getUsername())) {
            logger.warn("[{}] agent exist", _userService.getCurrentUser(token));
            throw new GeneralException("Agent already exist.", HttpStatus.BAD_REQUEST);
        }

        Agent agent = createNewAgent(request);
        Agent savedAgent = _agentRepository.save(agent);

        return mapAgentToAgentResponse(savedAgent);
    }

    @Override
    public AgentResponse getAgent(UUID id) {
        Agent agent = _agentRepository.findOneById(id);
        return mapAgentToAgentResponse(agent);
    }

    @Override
    public AgentResponse getAgentFromToken(String token) {
        String agentUsername = _tokenUtils.getUsernameFromToken(token);
        User user = _userRepository.findOneByUsername(agentUsername);

        return getAgent(user.getId());
    }

    private AgentResponse mapAgentToAgentResponse(Agent savedAgent) {
        AgentResponse agentResponse = new AgentResponse();
        agentResponse.setId(savedAgent.getId());
        agentResponse.setFirstName(savedAgent.getFirstName());
        agentResponse.setLastName(savedAgent.getLastName());
        agentResponse.setUsername(savedAgent.getUsername());
        agentResponse.setUserRole(savedAgent.getUserRole().toString());
        agentResponse.setAddress(savedAgent.getAddress());
        return agentResponse;
    }

    private Agent createNewAgent(CreateAgentRequest request) {
        Agent agent = new Agent();
        agent.setDeleted(false);
        agent.setUsername(request.getUsername());
        agent.setPassword(_passwordEncoder.encode(request.getPassword()));
        agent.setUserRole(UserRole.AGENT);
        agent.setFirstName(request.getFirstName());
        agent.setLastName(request.getLastName());
        agent.setAddress(request.getAddress());
        addAuthoritiesAgent(agent);
        return agent;
    }

    private void addAuthoritiesAgent(Agent agent) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(_authorityRepository.findByName("ROLE_SIMPLE_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_AD_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_MESSAGE_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_AGENT"));
        authorities.add(_authorityRepository.findByName("ROLE_COMMENT_USER"));
        agent.setAuthorities(new HashSet<>(authorities));
    }

    private boolean isAgentExist(String username) {
        return _userRepository.findOneByUsername(username) != null;
    }

}
