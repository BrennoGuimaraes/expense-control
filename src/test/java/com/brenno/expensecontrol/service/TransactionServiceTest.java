package com.brenno.expensecontrol.service;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.entity.Transaction;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.TransactionType;
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

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getTransactionsShouldMapRepositoryResult() {
        var user = new Users();
        user.setId(7L);
        var transactions = List.of(new Transaction());
        var responses = List.of(new TransactionResponse("Lunch", 20.0, "FOOD_AND_DRINK", LocalDateTime.now()));

        when(transactionRepository.findByAccountId(7L)).thenReturn(transactions);
        when(transactionMapper.trasactionEntityToTransactionResponse(transactions)).thenReturn(responses);

        var result = transactionService.getTransactions(user);

        assertThat(result).isEqualTo(responses);
    }

    @Test
    void createTransactionShouldAttachAccountAndPersist() {
        var request = new TransactionRequest("Salary", 1500.0, TransactionType.INCOME, 3L);
        var account = new Account();
        account.setId(3L);
        var transaction = new Transaction();

        when(accountService.getAccountById(3L)).thenReturn(Optional.of(account));
        when(transactionMapper.transactionRequestToTransactionEntity(request)).thenReturn(transaction);

        transactionService.createTransaction(request);

        var captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().getAccount()).isEqualTo(account);
        assertThat(captor.getValue().getDate()).isNotNull();
    }

    @Test
    void createTransactionShouldThrowWhenAccountDoesNotExist() {
        var request = new TransactionRequest("Salary", 1500.0, TransactionType.INCOME, 3L);

        when(accountService.getAccountById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Account not found");
    }

    @Test
    void importCsvShouldParseAndSaveTransactions() {
        var csvContent = """
                description,amount,type,date
                Market,55.4,Groceries,2026-04-20
                Fuel,100.0,Gas & Fuel,2026-04-19
                """;
        var file = new MockMultipartFile("file", "transactions.csv", "text/csv", csvContent.getBytes());

        transactionService.importCsv(file, 9L);

        var captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
        var firstTransaction = (Transaction) captor.getValue().getFirst();
        assertThat(firstTransaction.getDescription()).isEqualTo("Market");
        assertThat(firstTransaction.getType()).isEqualTo(TransactionType.GROCERIES);
        assertThat(firstTransaction.getAccount().getId()).isEqualTo(9L);
    }

    @Test
    void importCsvShouldWrapParsingErrors() {
        var csvContent = """
                description,amount,type,date
                Invalid,abc,Groceries,2026-04-20
                """;
        var file = new MockMultipartFile("file", "transactions.csv", "text/csv", csvContent.getBytes());

        assertThatThrownBy(() -> transactionService.importCsv(file, 9L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error processing CSV");
    }

    @Test
    void getTransactionTypesShouldReturnPercentagesSortedByFrequency() {
        var user = new Users();
        var transactions = List.of(
                new TransactionResponse("Lunch", 20.0, "FOOD_AND_DRINK", LocalDateTime.now()),
                new TransactionResponse("Dinner", 30.0, "FOOD_AND_DRINK", LocalDateTime.now()),
                new TransactionResponse("Fuel", 50.0, "GAS_AND_FUEL", LocalDateTime.now())
        );

        when(transactionRepository.findByAccountId(null)).thenReturn(List.of());
        when(transactionMapper.trasactionEntityToTransactionResponse(List.of())).thenReturn(transactions);

        var result = transactionService.getTransactionTypes(user);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().nameType()).isEqualTo("FOOD_AND_DRINK");
        assertThat(result.getFirst().percent()).isEqualTo(66.66666666666667);
        assertThat(result.get(1).nameType()).isEqualTo("GAS_AND_FUEL");
        assertThat(result.get(1).percent()).isEqualTo(33.333333333333336);
    }

    @Test
    void getTransactionTypesShouldReturnEmptyListWhenThereAreNoTransactions() {
        var user = new Users();

        when(transactionRepository.findByAccountId(null)).thenReturn(List.of());
        when(transactionMapper.trasactionEntityToTransactionResponse(List.of())).thenReturn(List.of());

        var result = transactionService.getTransactionTypes(user);

        assertThat(result).isEmpty();
    }
}
