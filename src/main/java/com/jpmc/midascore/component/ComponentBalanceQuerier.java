package com.jpmc.midascore.component;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@Component("componentBalanceQuerier")
public class ComponentBalanceQuerier implements BalanceQuerier {

    private final UserRepository userRepo;

    @Autowired
    public ComponentBalanceQuerier(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Balance query(Long userId) {
        Optional<UserRecord> userOpt = userRepo.findById(userId);
        double balance = userOpt.map(UserRecord::getBalance).orElse(0.0);
        return new Balance(balance);
    }
}
