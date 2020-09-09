package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.entity.Authority;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.repository.IAuthorityRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.services.IAdminService;
import com.owasp.authenticationservice.util.enums.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminService implements IAdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final ISimpleUserRepository _simpleUserRepository;
    private final SimpleUserService _simpleUserService;
    private final IAuthorityRepository _authorityRepository;
    private final UserService _userService;

    public AdminService(ISimpleUserRepository simpleUserRepository, SimpleUserService simpleUserService, IAuthorityRepository authorityRepository, UserService userService) {
        _simpleUserRepository = simpleUserRepository;
        _simpleUserService = simpleUserService;
        _authorityRepository = authorityRepository;
        _userService = userService;
    }

    @Override
    public List<SimpleUserResponse> approveRegistrationRequest(UUID id, String token) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        simpleUser.setUserStatus(UserStatus.APPROVED);
        simpleUser.setConfirmationTime(LocalDateTime.now());
        addAuthoritiesWhenApproved(simpleUser);
        _simpleUserRepository.save(simpleUser);
        logger.info("[{}] approve registration request ({})", _userService.getCurrentUser(token), simpleUser.getUsername());
        return _simpleUserService.getSimpleUserByStatus("PENDING", token);
    }

    private void addAuthoritiesWhenApproved(SimpleUser simpleUser) {
        Set<Authority> authorities = simpleUser.getRoles();
        authorities.add(_authorityRepository.findByName("ROLE_RENT_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_REQUEST_USER"));
        simpleUser.setAuthorities(authorities);
    }

    @Override
    public List<SimpleUserResponse> denyRegistrationRequest(UUID id, String token) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        simpleUser.setUserStatus(UserStatus.DENIED);
        _simpleUserRepository.save(simpleUser);
        logger.info("[{}] deny registration request ({})", _userService.getCurrentUser(token), simpleUser.getUsername());
        return _simpleUserService.getSimpleUserByStatus("PENDING", token);
    }

}
