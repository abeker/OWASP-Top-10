package com.owasp.authenticationservice.services.impl;

import com.owasp.authenticationservice.dto.request.CreateSimpleUserRequest;
import com.owasp.authenticationservice.dto.response.SimpleUserResponse;
import com.owasp.authenticationservice.dto.response.UserResponse;
import com.owasp.authenticationservice.entity.Authority;
import com.owasp.authenticationservice.entity.SimpleUser;
import com.owasp.authenticationservice.entity.User;
import com.owasp.authenticationservice.repository.IAuthorityRepository;
import com.owasp.authenticationservice.repository.ISimpleUserRepository;
import com.owasp.authenticationservice.repository.IUserRepository;
import com.owasp.authenticationservice.services.IUserService;
import com.owasp.authenticationservice.util.enums.UserRole;
import com.owasp.authenticationservice.util.enums.UserStatus;
import com.owasp.authenticationservice.util.exceptions.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService implements IUserService {

}
