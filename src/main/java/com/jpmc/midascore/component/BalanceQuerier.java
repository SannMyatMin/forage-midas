package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Balance;

public interface BalanceQuerier {
    Balance query(Long userId);
}