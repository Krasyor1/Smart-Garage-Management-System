package com.company.smartgarage.models.enums;

public enum Currency {
    BGN, EUR, USD, GBP;

    @Override
    public String toString() {
        return switch (this) {
            case EUR -> "EUR";
            case BGN -> "BGN";
            case USD -> "USD";
            case GBP -> "GBP";
            default -> throw new IllegalArgumentException();
        };
    }
}
