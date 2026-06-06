package com.qazaq.telecom.payment;


import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.account.AccountRepository;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.security.access.CurrentCustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {


    public final PaymentRepository paymentRepository;
    public final AccountRepository accountRepository;
    private final CurrentCustomerService currentCustomerService;


    @Transactional
    public void addPayment(Long accountId, PaymentRequest paymentRequest){
        Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new BusinessException("Account not found"));
        if (account.getCustomer() == null) {
            throw new BusinessException("Account is not linked to a customer");
        }
        currentCustomerService.requireCustomer(account.getCustomer().getId());

        if (paymentRequest == null || paymentRequest.getAmount() == null) {
            throw new BusinessException("Payment request is required");
        }
        if(paymentRequest.getAmount().compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException("Payment amount can not be lower than 0.0");
        }
        if(paymentRequest.getPaymentType() == null) {
            throw new BusinessException("Payment type is required");
        }
        if(paymentRequest.getTransactionType() == null) {
            throw new BusinessException("Transaction type is required");
        }

        Payment payment = Payment.builder()
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .transactionType(paymentRequest.getTransactionType())
                .account(account)
                .build();

        paymentRepository.save(payment);
    }

    @Transactional
    public GetPaymentRequest getPayment(Long payment_id){
        Payment payment = paymentRepository.findPaymentById(payment_id)
                .orElseThrow(() -> new BusinessException("Payment not found"));
        currentCustomerService.ensurePaymentOwner(payment);

        return GetPaymentRequest.builder()
                .amount(payment.getAmount())
                .transactionType(payment.getTransactionType())
                .paymentType(payment.getPaymentType())
                .createdAt(payment.getCreatedAt())
                .build();

    }
}
