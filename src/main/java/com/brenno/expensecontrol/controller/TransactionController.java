package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.dto.types.TypesResponse;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.mappers.transaction.TransactionMapper;
import com.brenno.expensecontrol.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;


    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
    }

    @GetMapping("types-percent")
    public ResponseEntity<List<TypesResponse>> getTypesWithPercernt(@AuthenticationPrincipal Users user) {

        return ResponseEntity.ok(transactionService.getTransactionTypes(user));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(@AuthenticationPrincipal Users user) {

        return ResponseEntity.ok(transactionService.getTransactions(user));
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest transactionRequest) {

        transactionService.createTransaction(transactionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body("Account saved successfully!");
    }


    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importCsv(@AuthenticationPrincipal Users user, @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().build();
        }

        transactionService.importCsv(file, user.getAccount().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body("CSV imported successfully!");
    }
}
