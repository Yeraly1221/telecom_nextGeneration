package com.qazaq.telecom.payment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPaymentRequest {

    private BigDecimal amount;

    private TransactionType transactionType;

    private PaymentType paymentType;

    private LocalDateTime createdAt;
}
