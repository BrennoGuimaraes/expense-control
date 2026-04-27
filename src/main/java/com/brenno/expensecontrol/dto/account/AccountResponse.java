package com.brenno.expensecontrol.dto.account;


import com.brenno.expensecontrol.dto.transaction.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public record AccountResponse(String nome, BigDecimal balance , List<TransactionResponse> transactions ) {
}
