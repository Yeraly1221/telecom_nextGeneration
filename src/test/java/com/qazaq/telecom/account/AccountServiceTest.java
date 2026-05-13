package com.qazaq.telecom.account;

import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.payment.PaymentRepository;
import com.qazaq.telecom.payment.PaymentRequest;
import com.qazaq.telecom.payment.PaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private AccountService accountService;

    @Test
    void withDrawBalanceShouldSubtractAndSaveUpdatedAccount() {
        Account account = Account.builder().id(1L).balance(100.0).build();
        when(accountRepository.findAccountById(1L)).thenReturn(Optional.of(account));

        accountService.withDrawBalance(1L, PaymentRequest.builder()
                .amount(40.0)
                .paymentType(PaymentType.CARD)
                .build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(60.0, captor.getValue().getBalance());
    }

    @Test
    void withDrawBalanceShouldFailWhenInsufficientFunds() {
        Account account = Account.builder().id(1L).balance(20.0).build();
        when(accountRepository.findAccountById(1L)).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.withDrawBalance(1L, PaymentRequest.builder()
                        .amount(40.0)
                        .paymentType(PaymentType.CARD)
                        .build())
        );

        assertEquals("You do not have enough money on balance", exception.getMessage());
        verify(accountRepository, never()).save(account);
    }

    @Test
    void depositBalanceShouldAddAndSaveUpdatedAccount() {
        Account account = Account.builder().id(1L).balance(25.0).build();
        when(accountRepository.findAccountById(1L)).thenReturn(Optional.of(account));

        accountService.depositBalance(1L, PaymentRequest.builder()
                .amount(15.0)
                .paymentType(PaymentType.CARD)
                .build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(40.0, captor.getValue().getBalance());
    }

    @Test
    void depositBalanceShouldFailWhenAmountIsNegative() {
        Account account = Account.builder().id(1L).balance(25.0).build();
        when(accountRepository.findAccountById(1L)).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.depositBalance(1L, PaymentRequest.builder()
                        .amount(-1.0)
                        .paymentType(PaymentType.CASH)
                        .build())
        );

        assertEquals("amount can not be lower then 0", exception.getMessage());
        verify(accountRepository, never()).save(account);
    }
}
