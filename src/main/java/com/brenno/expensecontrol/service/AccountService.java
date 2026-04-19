package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.account.AccountRequest;
import com.brenno.expensecontrol.dto.account.AccountResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.mappers.account.AccountMapper;
import com.brenno.expensecontrol.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public void saveAccount(AccountRequest accountRequest){
        var account = accountMapper.accountRequestToAccountEntity(accountRequest);
        accountRepository.save(account);

    }

    public List<AccountResponse> getAccounts(){
        return accountMapper.accountEntityToResponse(accountRepository.findAll());
    }

    public Optional<Account> getAccountById(Long id){
        return accountRepository.findById(id);
    }
}
