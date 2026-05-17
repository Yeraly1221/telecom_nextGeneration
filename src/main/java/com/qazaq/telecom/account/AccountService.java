package com.qazaq.telecom.account;


import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.payment.Payment;
import com.qazaq.telecom.payment.PaymentRepository;
import com.qazaq.telecom.payment.PaymentRequest;
import com.qazaq.telecom.payment.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    public final AccountRepository accountRepository;
    public final PaymentRepository paymentRepository;

    @Transactional
    public void withDrawBalance(Long id, PaymentRequest paymentRequest){
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));

        Double balance = account.getBalance();
        Double price = paymentRequest.getAmount();


        if(price < 0.0){
            throw new  BusinessException("price can not be lower then 0");
        }
        if(balance - price < 0.0){
            throw new BusinessException("You do not have enough money on balance");
        }

        Payment payment = Payment.builder()
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .transactionType(TransactionType.WITHDRAW)
                .account(account)
                .build();
        paymentRepository.save(payment);


        account.setBalance(balance - price);
        accountRepository.save(account);
    }

    @Transactional
    public void  depositBalance(Long id, PaymentRequest paymentRequest){
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));

        Double balance = account.getBalance();
        Double amount = paymentRequest.getAmount();


        if(amount < 0.0){
            throw new  BusinessException("amount can not be lower then 0");
        }

        Payment payment = Payment.builder()
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .transactionType(TransactionType.DEPOSIT)
                .account(account)
                .build();
        paymentRepository.save(payment);


        account.setBalance(balance + amount);
        accountRepository.save(account);
    }

    public AccountRequest getBalance(Long id){
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));

        return AccountRequest.builder()
                .amount(account.getBalance())
                .build();
    }





}
