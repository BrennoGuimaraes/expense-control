package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Categories;
import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.mappers.transaction.TransactionMapper;
import com.brenno.expensecontrol.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private CategoriesService categoriesService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getTransactionsShouldMapRepositoryResult() {
        var user = userWithId(7L);
        var transactions = List.of(new Transaction());
        var responses = List.of(transactionResponse("Lunch", 20.0, "Food & Drink"));

        when(transactionRepository.findByAccountId(7L)).thenReturn(transactions);
        when(transactionMapper.trasactionEntityToTransactionResponse(transactions)).thenReturn(responses);

        var result = transactionService.getTransactions(user);

        assertThat(result).isEqualTo(responses);
    }

    @Test
    void createTransactionShouldAttachAccountAndPersist() {
        var request = transactionRequest(3L);
        var account = accountWithId(3L);
        var category = category(8L, "Income");
        var transaction = new Transaction();

        when(accountService.getAccountById(3L)).thenReturn(Optional.of(account));
        when(categoriesService.getCategoriesById(8L)).thenReturn(category);
        when(transactionMapper.transactionRequestToTransactionEntity(request)).thenReturn(transaction);

        transactionService.createTransaction(request);

        var captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getAccount()).isEqualTo(account);
        assertThat(captor.getValue().getCategory()).isEqualTo(category);
        assertThat(captor.getValue().getDate()).isNotNull();
    }

    @Test
    void createTransactionShouldThrowWhenAccountDoesNotExist() {
        var request = transactionRequest(3L);

        when(accountService.getAccountById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Account not found");
    }

    @Test
    void importCsvShouldParseAndSaveTransactions() {
        var file = csvFile("""
                description,amount,type,date
                Market,55.4,Groceries,2026-04-20
                Fuel,100.0,Gas & Fuel,2026-04-19
                """);
        when(categoriesService.getCategoriesByLabel("Groceries")).thenReturn(category(1L, "Groceries"));
        when(categoriesService.getCategoriesByLabel("Gas & Fuel")).thenReturn(category(2L, "Gas & Fuel"));

        transactionService.importCsv(file, 9L);

        var captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        var firstTransaction = (Transaction) captor.getValue().getFirst();
        assertThat(firstTransaction.getDescription()).isEqualTo("Market");
        assertThat(firstTransaction.getCategory().getLabel()).isEqualTo("Groceries");
        assertThat(firstTransaction.getAccount().getId()).isEqualTo(9L);
    }

    @Test
    void importCsvShouldWrapParsingErrors() {
        var file = csvFile("""
                description,amount,type,date
                Invalid,abc,Groceries,2026-04-20
                """);

        assertThatThrownBy(() -> transactionService.importCsv(file, 9L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error processing CSV");
    }

    @Test
    void getTransactionCategoriesWithPercentShouldReturnPercentagesSortedByFrequency() {
        var user = userWithAccountId(10L);
        var food = category(1L, "Food & Drink");
        var fuel = category(2L, "Gas & Fuel");
        var transactions = List.of(
                transaction(1L, "Lunch", 20.0, food),
                transaction(2L, "Dinner", 30.0, food),
                transaction(3L, "Fuel", 50.0, fuel)
        );

        when(transactionRepository.findByAccountId(10L)).thenReturn(transactions);

        var result = transactionService.getTransactionCategoriesWithPercent(user);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().category()).isEqualTo("Food & Drink");
        assertThat(result.getFirst().percent()).isEqualTo(66.66666666666667);
        assertThat(result.get(1).category()).isEqualTo("Gas & Fuel");
        assertThat(result.get(1).percent()).isEqualTo(33.333333333333336);
    }

    @Test
    void getTransactionCategoriesWithPercentShouldReturnEmptyListWhenThereAreNoTransactions() {
        var user = userWithAccountId(10L);

        when(transactionRepository.findByAccountId(10L)).thenReturn(List.of());

        var result = transactionService.getTransactionCategoriesWithPercent(user);

        assertThat(result).isEmpty();
    }

    private TransactionRequest transactionRequest(Long accountId) {
        return new TransactionRequest("Salary", 1500.0, 8L, accountId);
    }

    private TransactionResponse transactionResponse(String description, Double amount, String category) {
        return new TransactionResponse(description, amount, category, LocalDateTime.now());
    }

    private Transaction transaction(Long id, String description, Double amount, Categories category) {
        return new Transaction(id, description, amount, LocalDateTime.now(), null, category);
    }

    private Categories category(Long id, String label) {
        return new Categories(id, label);
    }

    private Account accountWithId(Long id) {
        var account = new Account();
        account.setId(id);
        return account;
    }

    private Users userWithId(Long id) {
        var user = new Users();
        user.setId(id);
        return user;
    }

    private Users userWithAccountId(Long accountId) {
        var user = new Users();
        user.setAccount(accountWithId(accountId));
        return user;
    }

    private MockMultipartFile csvFile(String content) {
        return new MockMultipartFile("file", "transactions.csv", "text/csv", content.getBytes());
    }
}
