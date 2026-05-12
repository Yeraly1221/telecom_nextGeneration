package com.qazaq.telecom.account;


import com.qazaq.telecom.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    public final AccountRepository accountRepository;

    public void withDrawBalance(Long id, Double price){
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));

        Double balance = account.getBalance();


        if(price < 0.0){
            throw new  BusinessException("price can not be lower then 0");
        }
        if(balance - price < 0.0){
            throw new BusinessException("You do not have enough money on balance");
        }

        account.setBalance(balance - price);
        accountRepository.save(account);
    }

    public void  depositBalance(Long id, Double amount){
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));

        Double balance = account.getBalance();


        if(amount < 0.0){
            throw new  BusinessException("amount can not be lower then 0");
        }

        account.setBalance(balance + amount);
        accountRepository.save(account);
    }

    public AccountRequest getBalance(Long id){
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));

        return AccountRequest.builder()
                .balance(account.getBalance())
                .build();
    }

}
