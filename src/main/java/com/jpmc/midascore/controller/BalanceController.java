package com.jpmc.midascore.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        Optional<UserRecord> userOpt = userRepo.findById(userId);
        double balance = userOpt.map(UserRecord::getBalance).orElse(0.0);
        return new Balance(balance);
    }
}
