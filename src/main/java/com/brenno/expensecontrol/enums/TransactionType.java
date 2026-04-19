package com.brenno.expensecontrol.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    INCOME("Income"),
    TRANSFER("Transfer"),
    FOOD_AND_DRINK("Food & Drink"),
    GAS_AND_FUEL("Gas & Fuel"),
    HEALTH_AND_FITNESS("Health & Fitness"),
    NEWSSTAND("Newsstand"),
    ONLINE_SERVICES("Online Services"),
    SHOPPING("Shopping"),
    EDUCATION("Education"),
    GROCERIES("Groceries"),
    INSURANCE("Insurance"),
    DONATIONS_AND_CHARITY("Donations & Charity"),
    PET_CARE("Pet Care"),
    TRANSPORT("Transport");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

}