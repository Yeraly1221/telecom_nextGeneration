package com.qazaq.telecom.account;


import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.payment.Payment;
import com.qazaq.telecom.payment.PaymentRepository;
import com.qazaq.telecom.payment.PaymentRequest;
import com.qazaq.telecom.payment.TransactionType;
import com.qazaq.telecom.security.access.CurrentCustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    public final AccountRepository accountRepository;
    public final PaymentRepository paymentRepository;
    private final CurrentCustomerService currentCustomerService;

    @Transactional
    public void withDrawBalance(Long id, PaymentRequest paymentRequest){
        if (paymentRequest == null || paymentRequest.getAmount() == null || paymentRequest.getPaymentType() == null) {
            throw new BusinessException("Payment request is required");
        }
        Account account = accountRepository.findAccountById(id)
                .orElseThrow(() -> new BusinessException("Account not found"));
        if (account.getCustomer() == null) {
            throw new BusinessException("Account is not linked to a customer");
        }
        currentCustomerService.requireCustomer(account.getCustomer().getId());

        BigDecimal balance = account.getBalance();
        BigDecimal price = paymentRequest.getAmount();


        if(price.compareTo(BigDecimal.ZERO) < 0){
            throw new  BusinessException("price can not be lower then 0");
        }
        if(balance.compareTo(price) < 0){
            throw new BusinessException("You do not have enough money on balance");
        }

        Payment payment = Payment.builder()
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .transactionType(TransactionType.WITHDRAW)
                .account(account)
                .build();
        paymentRepository.save(payment);


        account.setBalance(balance.subtract(price));
        accountRepository.save(account);
    }

    @Transactional
    public void  depositBalance(Long id, PaymentRequest paymentRequest){
        if (paymentRequest == null || paymentRequest.getAmount() == null || paymentRequest.getPaymentType() == null) {
            throw new BusinessException("Payment request is required");
        }
        Account account = currentCustomerService.requireAccount(id);

        BigDecimal balance = account.getBalance();
        BigDecimal amount = paymentRequest.getAmount();


        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new  BusinessException("amount can not be lower then 0");
        }

        Payment payment = Payment.builder()
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .transactionType(TransactionType.DEPOSIT)
                .account(account)
                .build();
        paymentRepository.save(payment);


        account.setBalance(balance.add(amount));
        accountRepository.save(account);
    }

    public AccountRequest getBalance(Long id){
        Account account = currentCustomerService.requireAccount(id);

        return AccountRequest.builder()
                .amount(account.getBalance())
                .build();
    }





}
