package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void saveAccount(Account account){
        accountRepository.save(account);

    }

    public List<Account> getAccounts(){
        return accountRepository.findAll();

    }
}
