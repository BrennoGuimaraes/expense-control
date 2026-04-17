package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.mappers.transaction.TransactionMapper;
import com.brenno.expensecontrol.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(){

        var transactionResponse = transactionMapper.trasactionEntityToTransactionResponse(transactionService.getTransactions());

        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest transactionRequest){

        transactionService.createTransaction(transactionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("Account saved successfully!");
    }
}
