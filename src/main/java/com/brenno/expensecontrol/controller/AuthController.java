package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.users.UsersRequest;
import com.brenno.expensecontrol.dto.users.UsersResponse;
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


    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<UsersResponse> register(@RequestBody UsersRequest userRequest) {

        return ResponseEntity.ok(userService.register(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<UsersResponse> login(@RequestBody UsersRequest userRequest) {

        return ResponseEntity.ok(userService.login(userRequest));
    }
}

