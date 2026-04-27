package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.account.AccountRequest;
import com.brenno.expensecontrol.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")
public class AccountController {


    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountRequest accountRequest){

        accountService.saveAccount(accountRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("Account saved successfully!");
    }

//    @GetMapping
//    public ResponseEntity<List<AccountResponse>> getAccounts(){
//
//        return ResponseEntity.ok(accountService.getAccounts());
//
//    }

}
