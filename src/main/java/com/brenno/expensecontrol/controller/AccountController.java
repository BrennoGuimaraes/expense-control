package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.account.AccountRequest;
import com.brenno.expensecontrol.dto.account.AccountResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.mappers.account.AccountMapper;
import com.brenno.expensecontrol.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/members")
public class AccountController {

    private final AccountMapper accountMapper;

    private final AccountService accountService;

    public AccountController(AccountMapper accountMapper, AccountService accountService) {
        this.accountMapper = accountMapper;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountRequest accountRequest){

        Account account = accountMapper.accountRequestToAccountEntity(accountRequest);

        accountService.saveAccount(account);

        return ResponseEntity.status(HttpStatus.CREATED).body("Account saved successfully!");
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts(){

        List<AccountResponse> accounts = accountMapper.accountEntityToResponse(accountService.getAccounts());

        return ResponseEntity.ok(accounts);

    }

}
