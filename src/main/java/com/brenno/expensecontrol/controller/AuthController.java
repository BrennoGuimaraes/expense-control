package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.users.UsersRequest;
import com.brenno.expensecontrol.dto.users.UsersResponse;
import com.brenno.expensecontrol.mappers.users.UsersMapper;
import com.brenno.expensecontrol.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class AuthController {

    private final UserService userService;

    private final UsersMapper usersMapper;

    public AuthController(UserService userService, UsersMapper usersMapper) {
        this.userService = userService;
        this.usersMapper = usersMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UsersResponse> register(@RequestBody UsersRequest request) {

        var user = usersMapper.usersRequestToUsersEntity(request);

        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UsersResponse> login(@RequestBody UsersRequest request) {
        var user = usersMapper.usersRequestToUsersEntity(request);

        return ResponseEntity.ok(userService.login(user));
    }
}

