package com.brenno.expensecontrol.dto.transaction;

import com.brenno.expensecontrol.enums.TransactionType;


public record TransactionRequest(String description, Double amount, TransactionType type, Long idAccount) {
}
