package com.company.smartgarage.models.dtos;

import com.company.smartgarage.models.enums.Currency;

public class CurrencyDto {
    private Currency currency;
    public CurrencyDto() {
        currency = Currency.BGN;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
