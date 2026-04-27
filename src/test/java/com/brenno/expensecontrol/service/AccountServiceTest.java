package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.account.AccountRequest;
import com.brenno.expensecontrol.dto.account.AccountResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.mappers.account.AccountMapper;
import com.brenno.expensecontrol.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void saveAccountShouldMapAndPersistEntity() {
        var request = new AccountRequest("Main account", BigDecimal.TEN);
        var account = new Account();

        when(accountMapper.accountRequestToAccountEntity(request)).thenReturn(account);

        accountService.saveAccount(request);

        verify(accountMapper).accountRequestToAccountEntity(request);
        verify(accountRepository).save(account);
    }

//    @Test
//    void getAccountsShouldReturnMappedResponses() {
//        var accounts = List.of(new Account());
//        var responses = List.of(new AccountResponse("Main account", BigDecimal.TEN, List.of()));
//
//        when(accountRepository.findAll()).thenReturn(accounts);
//        when(accountMapper.accountEntityToResponse(accounts)).thenReturn(responses);
//
//        var result = accountService.getAccounts();
//
//        assertThat(result).isEqualTo(responses);
//    }

    @Test
    void getAccountByIdShouldDelegateToRepository() {
        var account = new Account();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        var result = accountService.getAccountById(1L);

        assertThat(result).contains(account);
    }
}
