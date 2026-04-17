package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactions(){
        return transactionRepository.findAll();
    }
}
