package com.qazaq.telecom.account;


import com.qazaq.telecom.payment.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final  AccountService accountService;

    @PostMapping("/deposite/{customerId}")
    public void DepositBalance(@PathVariable Long customerId, @Valid @RequestBody PaymentRequest paymentRequest) {
        accountService.depositBalance(customerId, paymentRequest);
    }

    @GetMapping("/balance/{id}")
    public AccountRequest GetBalance(@PathVariable Long id){
        return accountService.getBalance(id);
    }
}
