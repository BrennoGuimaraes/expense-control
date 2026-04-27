package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.categories.CategoriesResponseWithPercent;
import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.UserRoles;
import com.brenno.expensecontrol.mappers.transaction.TransactionMapper;
import com.brenno.expensecontrol.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionMapper transactionMapper;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransactionController(transactionService, transactionMapper))
                .setCustomArgumentResolvers(new AuthenticatedUserArgumentResolver())
                .build();
    }

    @Test
    void getCategoriesPercentShouldReturnServiceResponse() throws Exception {
        var user = authenticatedUser();

        when(transactionService.getTransactionCategoriesWithPercent(user)).thenReturn(List.of(
                categoriesResponseWithPercent(60.0, "Food & Drink")
        ));

        mockMvc.perform(get("/transaction/categories-percent").requestAttr("authenticatedUser", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percent").value(60.0))
                .andExpect(jsonPath("$[0].category").value("Food & Drink"));
    }

    @Test
    void getTransactionsShouldReturnServiceResponse() throws Exception {
        var user = authenticatedUser();

        when(transactionService.getTransactions(user)).thenReturn(List.of(
                transactionResponse("Lunch", 20.0, "Food & Drink")
        ));

        mockMvc.perform(get("/transaction").requestAttr("authenticatedUser", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Lunch"))
                .andExpect(jsonPath("$[0].amount").value(20.0))
                .andExpect(jsonPath("$[0].category").value("Food & Drink"));
    }

    @Test
    void createTransactionShouldReturnCreated() throws Exception {
        var request = transactionRequest(5L);

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction saved successfully!"));

        verify(transactionService).createTransaction(request);
    }

    @Test
    void importCsvShouldRejectEmptyFile() throws Exception {
        var user = authenticatedUser();
        var file = new MockMultipartFile("file", "transactions.csv", "text/csv", new byte[0]);

        mockMvc.perform(multipart("/transaction/import-csv")
                        .file(file)
                        .requestAttr("authenticatedUser", user))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsvShouldRejectNonCsvFile() throws Exception {
        var user = authenticatedUser();
        var file = new MockMultipartFile("file", "transactions.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/transaction/import-csv")
                        .file(file)
                        .requestAttr("authenticatedUser", user))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsvShouldDelegateAndReturnCreated() throws Exception {
        var user = authenticatedUser();
        var file = new MockMultipartFile("file", "transactions.csv", "text/csv", "header\nvalue".getBytes());

        mockMvc.perform(multipart("/transaction/import-csv")
                        .file(file)
                        .requestAttr("authenticatedUser", user))
                .andExpect(status().isCreated())
                .andExpect(content().string("CSV imported successfully!"));

        verify(transactionService).importCsv(file, 11L);
    }

    private Users authenticatedUser() {
        var account = new Account(11L, "Main account", BigDecimal.ZERO, List.of(), null);
        return new Users(1L, "brenno", "Brenno", "secret", account, UserRoles.USER);
    }

    private TransactionRequest transactionRequest(Long accountId) {
        return new TransactionRequest("Salary", 1500.0, 8L, accountId);
    }

    private TransactionResponse transactionResponse(String description, Double amount, String category) {
        return new TransactionResponse(description, amount, category, LocalDateTime.of(2026, 4, 20, 12, 0));
    }

    private CategoriesResponseWithPercent categoriesResponseWithPercent(Double percent, String category) {
        return new CategoriesResponseWithPercent(percent, category);
    }

    private static final class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                    && Users.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest,
                                      org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
            return webRequest.getAttribute("authenticatedUser", NativeWebRequest.SCOPE_REQUEST);
        }
    }
}
