package com.qazaq.telecom.account;



import com.qazaq.telecom.customer.Customer;
import com.qazaq.telecom.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "balance")
    private Double balance;

    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER)
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    List<Payment> payments = new ArrayList<>();
}
