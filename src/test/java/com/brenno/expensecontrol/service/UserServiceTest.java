package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.users.UsersRequest;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.UserRoles;
import com.brenno.expensecontrol.mappers.users.UsersMapper;
import com.brenno.expensecontrol.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @Test
    void registerShouldEncodePasswordCreateAccountAndReturnToken() {
        var request = new UsersRequest("brenno", "Brenno", "123456");
        var user = new Users();
        user.setLogin("brenno");
        user.setName("Brenno");
        user.setPassword("123456");

        when(usersMapper.usersRequestToUsersEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        var response = userService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getRole()).isEqualTo(UserRoles.USER);
        assertThat(user.getAccount()).isNotNull();
        assertThat(user.getAccount().getName()).isEqualTo("Brenno");
        verify(usersRepository).save(user);
    }

    @Test
    void loginShouldAuthenticateLoadUserAndReturnToken() {
        var request = new UsersRequest("brenno", "Brenno", "123456");
        var loginPayload = new Users();
        loginPayload.setLogin("brenno");
        loginPayload.setPassword("123456");

        var persistedUser = new Users();
        persistedUser.setLogin("brenno");

        when(usersMapper.usersRequestToUsersEntity(request)).thenReturn(loginPayload);
        when(usersRepository.findByLogin("brenno")).thenReturn(Optional.of(persistedUser));
        when(jwtService.generateToken(persistedUser)).thenReturn("jwt-token");

        var response = userService.login(request);

        var authenticationCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertThat(authenticationCaptor.getValue().getPrincipal()).isEqualTo("brenno");
        assertThat(authenticationCaptor.getValue().getCredentials()).isEqualTo("123456");
        assertThat(response.token()).isEqualTo("jwt-token");
    }
}
