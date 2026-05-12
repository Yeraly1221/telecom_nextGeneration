package com.qazaq.telecom.payment;


import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.account.AccountRepository;
import com.qazaq.telecom.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {


    public final PaymentRepository paymentRepository;
    public final AccountRepository accountRepository;


    public void addPayment(Long account_id, PaymentRequest paymentRequest){
        Account account = accountRepository.findAccountById(account_id)
                .orElseThrow(() -> new BusinessException("Account not found"));


        if(paymentRequest.getAmount() < 0.0){
            throw new BusinessException("Payment amount can not be lower than 0.0");
        }


        Payment payment = Payment.builder()
                .amount(paymentRequest.getAmount())
                .paymentType(paymentRequest.getPaymentType())
                .transactionType(paymentRequest.getTransactionType())
                .account(account)
                .build();


        paymentRepository.save(payment);
    }
}
