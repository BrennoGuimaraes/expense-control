package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.dto.types.TypesResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.TransactionType;
import com.brenno.expensecontrol.mappers.transaction.TransactionMapper;
import com.brenno.expensecontrol.repository.TransactionRepository;
import com.opencsv.CSVReaderBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<TransactionResponse> getTransactions(Users user) {

        var transactions = transactionRepository.findByAccountId(user.getId());

        return transactionMapper.trasactionEntityToTransactionResponse(transactions);
    }

    public void createTransaction(TransactionRequest transactionRequest) {
        var account = accountService.getAccountById(transactionRequest.idAccount()).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        var transaction = transactionMapper.transactionRequestToTransactionEntity(transactionRequest);

        transaction.setAccount(account);
        transaction.setDate(LocalDateTime.now());

        transactionRepository.save(transaction);

    }

    public void importCsv(MultipartFile file, Long idAccount) {
        var imported = new ArrayList<Transaction>();

        try (
                var reader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
        ) {
            var csv = new CSVReaderBuilder(reader).withSkipLines(1).build();

            String[] line;
            int lineNumber = 2;

            while ((line = csv.readNext()) != null) {
                var description = line[0].trim();
                var amount = Double.parseDouble(line[1].trim());
                var type = line[2].trim();
                var date = LocalDate.parse(line[3].trim()).atStartOfDay();
                TransactionType category = Arrays.stream(TransactionType.values())
                        .filter(c -> c.getLabel().equals(type))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unknown category: " + type));


                var expense = new Transaction(null, description, amount, category, date, new Account(idAccount, null, null, null, null));

                imported.add(expense);

                lineNumber++;
            }

            transactionRepository.saveAll(imported);

        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV. " + e.getMessage());
        }

    }

    public List<TypesResponse> getTransactionTypes(Users user) {
        var transactions = getTransactions(user);

        long total = transactions.size();

        if (total == 0) return Collections.emptyList();

        return transactions.stream()
                .collect(Collectors.groupingBy(TransactionResponse::type, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Double percent = entry.getValue() * 100.0 / total;
                    String nameType = entry.getKey();
                    return new TypesResponse(percent, nameType);
                })
                .collect(Collectors.toList());
    }
}
