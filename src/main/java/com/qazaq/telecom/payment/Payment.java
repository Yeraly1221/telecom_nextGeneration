package com.qazaq.telecom.payment;


import com.qazaq.telecom.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            name = "amount")
    private BigDecimal amount;

    @Column(name = "transaction_type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;


    @Column(name = "payment_type",
            nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;




    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

}
