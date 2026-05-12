package com.qazaq.telecom.subscription;


import com.qazaq.telecom.simcard.SimCard;
import com.qazaq.telecom.tariff.Tariff;
import com.qazaq.telecom.usagerecords.UsageRecords;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "tariff_name",
    nullable = false)
    private String tariffName;


    @Column(name = "remaining_megabyte",
            nullable = false)
    private Integer megabyte;


    @Column(name = "remaining_sms",
            nullable = false)
    private Integer sms;


    @Column(name = "remaining_minutes",
            nullable = false)
    private Integer minutes;


    @Enumerated(EnumType.STRING)
    @Column(name = "subscriptionStatus")
    private SubscriptionStatus subscriptionStatus;


    @Column(name = "start_date")
    private LocalDateTime startDate;


    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "simCard_id")
    private SimCard simCard;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<UsageRecords> usageRecords = new ArrayList<>();

    public void addUsageRecord(UsageRecords usageRecord){
        this.usageRecords.add(usageRecord);
    }
}
