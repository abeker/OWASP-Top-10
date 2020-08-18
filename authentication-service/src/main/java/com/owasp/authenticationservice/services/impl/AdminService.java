package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.entity.Authority;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.repository.IAdminRepository;
import com.owasp.authenticationservice.repository.IAuthorityRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.services.IAdminService;
import com.owasp.authenticationservice.util.enums.UserStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService implements IAdminService {

    private final IAdminRepository _adminRepository;
    private final IUserRepository _userRepository;
    private final ISimpleUserRepository _simpleUserRepository;
    private final SimpleUserService _simpleUserService;
    private final IAuthorityRepository _authorityRepository;

    public AdminService(IAdminRepository adminRepository, IUserRepository userRepository, ISimpleUserRepository simpleUserRepository, SimpleUserService simpleUserService, IAuthorityRepository authorityRepository) {
        _adminRepository = adminRepository;
        _userRepository = userRepository;
        _simpleUserRepository = simpleUserRepository;
        _simpleUserService = simpleUserService;
        _authorityRepository = authorityRepository;
    }

    @Override
    public List<SimpleUserResponse> approveRegistrationRequest(UUID id) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        simpleUser.setUserStatus(UserStatus.APPROVED);
        simpleUser.setConfirmationTime(LocalDateTime.now());
        addAuthoritiesWhenApproved(simpleUser);
        _simpleUserRepository.save(simpleUser);
        return _simpleUserService.getSimpleUserByStatus("PENDING");
    }

    private void addAuthoritiesWhenApproved(SimpleUser simpleUser) {
        Set<Authority> authorities = simpleUser.getRoles();
        authorities.add(_authorityRepository.findByName("ROLE_RENT_USER"));
        authorities.add(_authorityRepository.findByName("ROLE_REQUEST_USER"));
        simpleUser.setAuthorities(authorities);
    }

    @Override
    public List<SimpleUserResponse> denyRegistrationRequest(UUID id) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        simpleUser.setUserStatus(UserStatus.DENIED);
        _simpleUserRepository.save(simpleUser);
        return _simpleUserService.getSimpleUserByStatus("PENDING");
    }
}
