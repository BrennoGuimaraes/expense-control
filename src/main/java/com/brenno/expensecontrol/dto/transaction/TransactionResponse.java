package com.brenno.expensecontrol.dto.transaction;

import com.brenno.expensecontrol.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(String description, Double amount, TransactionType type, LocalDateTime date,
                                  Long accountId) {
}
