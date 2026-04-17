package com.brenno.expensecontrol.dto.account;


import com.brenno.expensecontrol.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public record AccountResponse(String nome, BigDecimal balance , List<Transaction>transactions) {
}
