package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Balance {
    private double amount;

    public Balance() {
    }

    public Balance(float amount) {
        this.amount = amount;
    }

    public Balance(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "Balance{amount=" + amount + "}";
    }
}