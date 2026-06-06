package com.qazaq.telecom.payment;

import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.account.AccountRepository;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.customer.Customer;
import com.qazaq.telecom.security.access.CurrentCustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrentCustomerService currentCustomerService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void addPaymentShouldCreateAndSavePayment() {
        Customer customer = Customer.builder().id(7L).build();
        Account account = Account.builder().id(7L).balance(BigDecimal.valueOf(50)).customer(customer).build();
        customer.setAccount(account);
        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(20))
                .paymentType(PaymentType.CARD)
                .transactionType(TransactionType.DEPOSIT)
                .build();
        when(accountRepository.findAccountById(7L)).thenReturn(Optional.of(account));
        when(currentCustomerService.requireCustomer(7L)).thenReturn(customer);

        paymentService.addPayment(7L, request);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment savedPayment = captor.getValue();
        assertEquals(BigDecimal.valueOf(20), savedPayment.getAmount());
        assertEquals(PaymentType.CARD, savedPayment.getPaymentType());
        assertEquals(TransactionType.DEPOSIT, savedPayment.getTransactionType());
        assertSame(account, savedPayment.getAccount());
    }

    @Test
    void addPaymentShouldFailWhenAmountIsNegative() {
        Customer customer = Customer.builder().id(7L).build();
        Account account = Account.builder().id(7L).balance(BigDecimal.valueOf(50)).customer(customer).build();
        customer.setAccount(account);
        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(-5))
                .paymentType(PaymentType.CASH)
                .transactionType(TransactionType.WITHDRAW)
                .build();
        when(accountRepository.findAccountById(7L)).thenReturn(Optional.of(account));
        when(currentCustomerService.requireCustomer(7L)).thenReturn(customer);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.addPayment(7L, request)
        );

        assertEquals("Payment amount can not be lower than 0.0", exception.getMessage());
        verify(paymentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void addPaymentShouldFailWhenAccountDoesNotExist() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(5))
                .paymentType(PaymentType.ONLINE)
                .transactionType(TransactionType.DEPOSIT)
                .build();
        when(accountRepository.findAccountById(7L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.addPayment(7L, request)
        );

        assertEquals("Account not found", exception.getMessage());
        verify(paymentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
