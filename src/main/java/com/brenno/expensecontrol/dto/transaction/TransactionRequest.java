package com.brenno.expensecontrol.dto.transaction;


import java.math.BigDecimal;

public record TransactionRequest(String description, BigDecimal amount, Long idCategory, Long idAccount) {
}
