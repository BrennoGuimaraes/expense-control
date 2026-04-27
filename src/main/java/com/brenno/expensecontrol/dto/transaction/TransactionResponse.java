package com.brenno.expensecontrol.dto.transaction;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TransactionResponse(String description, Double amount, String category,
                                  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
                                  LocalDateTime date) {
}
