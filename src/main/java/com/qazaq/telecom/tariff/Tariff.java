package com.qazaq.telecom.tariff;


import com.qazaq.telecom.subscription.Subscription;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.objenesis.instantiator.sun.SunReflectionFactoryInstantiator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name",
            nullable = false)
    private String name;


    @Column(name = "price",
            nullable = false)
    private Double price;


    @Column(name = "internet_limit",
            nullable = false)
    private Integer megabyteLimit;


    @Column(name = "minutes_limit",
            nullable = false)
    private Integer minutesLimit;


    @Column(name = "sms_limit",
            nullable = false)
    private Integer smsLimit;


    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions = new ArrayList<>();

    public void addSubscription(Subscription subscription){
        this.subscriptions.add(subscription);
    }
}
