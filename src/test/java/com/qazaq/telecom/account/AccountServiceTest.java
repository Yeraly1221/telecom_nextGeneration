package com.qazaq.telecom.account;

import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.payment.PaymentRepository;
import com.qazaq.telecom.payment.PaymentRequest;
import com.qazaq.telecom.payment.PaymentType;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CurrentCustomerService currentCustomerService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void withDrawBalanceShouldSubtractAndSaveUpdatedAccount() {
        Customer customer = Customer.builder().id(1L).build();
        Account account = Account.builder().id(1L).balance(BigDecimal.valueOf(100)).customer(customer).build();
        customer.setAccount(account);
        when(accountRepository.findAccountById(1L)).thenReturn(Optional.of(account));
        when(currentCustomerService.requireCustomer(1L)).thenReturn(customer);

        accountService.withDrawBalance(1L, PaymentRequest.builder()
                .amount(BigDecimal.valueOf(40))
                .paymentType(PaymentType.CARD)
                .build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(BigDecimal.valueOf(60), captor.getValue().getBalance());
    }

    @Test
    void withDrawBalanceShouldFailWhenInsufficientFunds() {
        Customer customer = Customer.builder().id(1L).build();
        Account account = Account.builder().id(1L).balance(BigDecimal.valueOf(20)).customer(customer).build();
        customer.setAccount(account);
        when(accountRepository.findAccountById(1L)).thenReturn(Optional.of(account));
        when(currentCustomerService.requireCustomer(1L)).thenReturn(customer);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.withDrawBalance(1L, PaymentRequest.builder()
                        .amount(BigDecimal.valueOf(40))
                        .paymentType(PaymentType.CARD)
                        .build())
        );

        assertEquals("You do not have enough money on balance", exception.getMessage());
        verify(accountRepository, never()).save(account);
    }

    @Test
    void depositBalanceShouldAddAndSaveUpdatedAccount() {
        Customer customer = Customer.builder().id(1L).build();
        Account account = Account.builder().id(1L).balance(BigDecimal.valueOf(25)).customer(customer).build();
        customer.setAccount(account);
        when(currentCustomerService.requireAccount(1L)).thenReturn(account);

        accountService.depositBalance(1L, PaymentRequest.builder()
                .amount(BigDecimal.valueOf(15))
                .paymentType(PaymentType.CARD)
                .build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(BigDecimal.valueOf(40), captor.getValue().getBalance());
    }

    @Test
    void depositBalanceShouldFailWhenAmountIsNegative() {
        Customer customer = Customer.builder().id(1L).build();
        Account account = Account.builder().id(1L).balance(BigDecimal.valueOf(25)).customer(customer).build();
        customer.setAccount(account);
        when(currentCustomerService.requireAccount(1L)).thenReturn(account);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.depositBalance(1L, PaymentRequest.builder()
                        .amount(BigDecimal.valueOf(-1))
                        .paymentType(PaymentType.CASH)
                        .build())
        );

        assertEquals("amount can not be lower then 0", exception.getMessage());
        verify(accountRepository, never()).save(account);
    }
}
