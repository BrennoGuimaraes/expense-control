package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.users.UsersResponse;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.UserRoles;
import com.brenno.expensecontrol.repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepository usersRepository;

        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UsersResponse register(Users users) {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setRole(UserRoles.USER);
            usersRepository.save(users);
            return new UsersResponse(jwtService.generateToken(users));
    }

        public UsersResponse login(Users users) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(users.getLogin(), users.getPassword())
            );
            var user = usersRepository.findByLogin(users.getLogin()).orElseThrow();
            return new UsersResponse(jwtService.generateToken(user));
        }
    }

