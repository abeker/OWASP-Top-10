package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.repository.IAdminRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.services.IAdminService;
import com.owasp.authenticationservice.util.enums.UserStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements IAdminService {

    private final IAdminRepository _adminRepository;
    private final IUserRepository _userRepository;
    private final ISimpleUserRepository _simpleUserRepository;
    private final SimpleUserService _simpleUserService;

    public AdminService(IAdminRepository adminRepository, IUserRepository userRepository, ISimpleUserRepository simpleUserRepository, SimpleUserService simpleUserService) {
        _adminRepository = adminRepository;
        _userRepository = userRepository;
        _simpleUserRepository = simpleUserRepository;
        _simpleUserService = simpleUserService;
    }

    @Override
    public List<SimpleUserResponse> approveRegistrationRequest(UUID id) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        simpleUser.setUserStatus(UserStatus.APPROVED);
        _simpleUserRepository.save(simpleUser);
        return _simpleUserService.getSimpleUserByStatus("PENDING");
    }

    @Override
    public List<SimpleUserResponse> denyRegistrationRequest(UUID id) {
        SimpleUser simpleUser = _simpleUserRepository.findOneById(id);
        simpleUser.setUserStatus(UserStatus.DENIED);
        _simpleUserRepository.save(simpleUser);
        return _simpleUserService.getSimpleUserByStatus("PENDING");
    }
}
