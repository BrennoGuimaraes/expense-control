package com.brenno.expensecontrol.dto;

import java.math.BigDecimal;

public record AccountRequest(String name, BigDecimal balance) {
}
