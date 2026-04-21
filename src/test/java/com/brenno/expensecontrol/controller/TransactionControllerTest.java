package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.dto.types.TypesResponse;
import com.brenno.expensecontrol.entity.Account;
import com.brenno.expensecontrol.entity.Users;
import com.brenno.expensecontrol.enums.TransactionType;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void getTypesPercentShouldReturnServiceResponse() throws Exception {
        var user = authenticatedUser();

        when(transactionService.getTransactionTypes(user)).thenReturn(List.of(
                new TypesResponse(60.0, "FOOD_AND_DRINK")
        ));

        mockMvc.perform(get("/transaction/types-percent").requestAttr("authenticatedUser", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percent").value(60.0))
                .andExpect(jsonPath("$[0].nameType").value("FOOD_AND_DRINK"));
    }

    @Test
    void getTransactionsShouldReturnServiceResponse() throws Exception {
        var user = authenticatedUser();

        when(transactionService.getTransactions(user)).thenReturn(List.of(
                new TransactionResponse("Lunch", 20.0, "FOOD_AND_DRINK", LocalDateTime.of(2026, 4, 20, 12, 0))
        ));

        mockMvc.perform(get("/transaction").requestAttr("authenticatedUser", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Lunch"))
                .andExpect(jsonPath("$[0].amount").value(20.0))
                .andExpect(jsonPath("$[0].type").value("FOOD_AND_DRINK"));
    }

    @Test
    void createTransactionShouldReturnCreated() throws Exception {
        var request = new TransactionRequest("Salary", 1500.0, TransactionType.INCOME, 5L);

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Account saved successfully!"));

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
