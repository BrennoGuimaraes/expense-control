package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.categories.CategoriesResponseWithPercent;
import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.entity.Categories;
import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.entity.Users;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    private final TransactionMapper transactionMapper;

    private final CategoriesService categoriesService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService, TransactionMapper transactionMapper, CategoriesService categoriesService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.transactionMapper = transactionMapper;
        this.categoriesService = categoriesService;
    }

    public List<TransactionResponse> getTransactions(Users user) {

        var transactions = transactionRepository.findByAccountId(user.getId());

        return transactionMapper.trasactionEntityToTransactionResponse(transactions);
    }

    public void createTransaction(TransactionRequest transactionRequest) {
        var account = accountService.getAccountById(transactionRequest.idAccount()).orElseThrow(() -> new EntityNotFoundException("Account not found"));

        var category = categoriesService.getCategoriesById(transactionRequest.idCategory());

        var transaction = transactionMapper.transactionRequestToTransactionEntity(transactionRequest);

        transaction.setAccount(account);
        transaction.setCategory(category);
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
            while ((line = csv.readNext()) != null) {
                var description = line[0].trim();
                var amount = Double.parseDouble(line[1].trim());
                var type = line[2].trim();
                var date = LocalDate.parse(line[3].trim()).atStartOfDay();


                var expense = new Transaction();
                expense.setDescription(description);
                expense.setAmount(amount);
                expense.setDate(date);
                expense.setAccount(new Account(idAccount, null, null, null, null));
                var category = categoriesService.getCategoriesByLabel(type);
                expense.setCategory(new Categories(category.getId(), category.getLabel()));

                imported.add(expense);
            }

            transactionRepository.saveAll(imported);

        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV. " + e.getMessage());
        }

    }

    public List<CategoriesResponseWithPercent> getTransactionCategoriesWithPercent(Users user) {
        var accountId = user.getAccount().getId();
        var transactions = transactionRepository.findByAccountId(accountId);

        long total = transactions.size();

        if (total == 0) return Collections.emptyList();

        return transactions.stream()
                .filter(transaction -> transaction.getCategory() != null)
                .filter(transaction -> transaction.getCategory().getLabel() != null)
                .collect(Collectors.groupingBy(transaction -> transaction.getCategory().getLabel(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Double percent = entry.getValue() * 100.0 / total;
                    String category = entry.getKey();
                    return new CategoriesResponseWithPercent(percent, category);
                })
                .collect(Collectors.toList());
    }
}
