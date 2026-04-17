package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.mappers.transaction.TransactionMapper;
import com.brenno.expensecontrol.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionMapper = transactionMapper;
    }

    public List<Transaction> getTransactions(){
        return transactionRepository.findAll();
    }

    public void createTransaction(TransactionRequest transactionRequest){
        var account = accountService.getAccountById(transactionRequest.idAccount()).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        var transaction = transactionMapper.transactionRequestToTransactionEntity(transactionRequest);

        transaction.setAccount(account);
        transaction.setDate(LocalDateTime.now());

        transactionRepository.save(transaction);

    }
}
