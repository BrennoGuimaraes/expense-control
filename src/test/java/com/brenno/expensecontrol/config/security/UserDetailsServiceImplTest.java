package com.brenno.expensecontrol.config.security;

import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameShouldReturnUser() {
        var user = new Users();
        user.setLogin("brenno");

        when(usersRepository.findByLogin("brenno")).thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername("brenno");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        when(usersRepository.findByLogin("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
