package com.brenno.expensecontrol.dto.transaction;


public record TransactionRequest(String description, Double amount, Long idCategory, Long idAccount) {
}
