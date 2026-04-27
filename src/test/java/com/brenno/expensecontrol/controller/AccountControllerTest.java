package com.brenno.expensecontrol.controller;

import com.brenno.expensecontrol.dto.account.AccountRequest;
import com.brenno.expensecontrol.dto.account.AccountResponse;
import com.brenno.expensecontrol.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController(accountService)).build();
    }

    @Test
    void createAccountShouldReturnCreated() throws Exception {
        var request = new AccountRequest("Main account", BigDecimal.TEN);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Account saved successfully!"));

        verify(accountService).saveAccount(request);
    }

//    @Test
//    void getAccountsShouldReturnResponseList() throws Exception {
//        when(accountService.getAccounts()).thenReturn(List.of(
//                new AccountResponse("Main account", BigDecimal.TEN, List.of())
//        ));
//
//        mockMvc.perform(get("/account"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].nome").value("Main account"))
//                .andExpect(jsonPath("$[0].balance").value(10));
//    }
}
