package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.UserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "qH38vXUsnz84YkG5pjYHywW1d/5m0hQ5eGV8p6P05h8=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
    }

    @Test
    void shouldGenerateTokenAndExtractUsername() {
        var user = new Users(1L, "brenno", "Brenno", "secret", null, UserRoles.USER);

        var token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("brenno");
    }

    @Test
    void shouldValidateTokenForSameUser() {
        var user = new Users(1L, "brenno", "Brenno", "secret", null, UserRoles.USER);
        var token = jwtService.generateToken(user);

        var result = jwtService.isTokenValid(token, user);

        assertThat(result).isTrue();
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        var tokenOwner = new Users(1L, "brenno", "Brenno", "secret", null, UserRoles.USER);
        var anotherUser = new Users(2L, "alice", "Alice", "secret", null, UserRoles.USER);
        var token = jwtService.generateToken(tokenOwner);

        var result = jwtService.isTokenValid(token, anotherUser);

        assertThat(result).isFalse();
    }
}
