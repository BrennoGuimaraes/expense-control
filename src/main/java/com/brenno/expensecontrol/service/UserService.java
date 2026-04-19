package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.users.UsersRequest;
import com.brenno.expensecontrol.dto.users.UsersResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.enums.UserRoles;
import com.brenno.expensecontrol.mappers.users.UsersMapper;
import com.brenno.expensecontrol.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public UserService(UsersRepository usersRepository, UsersMapper usersMapper, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UsersResponse register(UsersRequest usersRequest) {

        var users = usersMapper.usersRequestToUsersEntity(usersRequest);

        users.setPassword(passwordEncoder.encode(users.getPassword()));
        users.setRole(UserRoles.USER);
        users.setName(users.getName());
        users.setAccount(new Account(null, users.getName(), null, null, null));
        usersRepository.save(users);
        return new UsersResponse(jwtService.generateToken(users));
    }

    public UsersResponse login(UsersRequest usersRequest) {

        var usersEntity = usersMapper.usersRequestToUsersEntity(usersRequest);


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usersEntity.getLogin(), usersEntity.getPassword())
        );
        var user = usersRepository.findByLogin(usersEntity.getLogin()).orElseThrow();
        return new UsersResponse(jwtService.generateToken(user));
    }
}

