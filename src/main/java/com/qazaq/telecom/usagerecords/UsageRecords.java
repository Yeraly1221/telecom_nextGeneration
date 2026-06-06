package com.qazaq.telecom.usagerecords;


import com.qazaq.telecom.subscription.Subscription;
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
@Table(name = "usage_records")
public class UsageRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false,
            name = "used_traffic")
    private Integer usedTraffic;

    @Column(nullable = false,
    name = "payed")
    private BigDecimal payed;


    @Enumerated(EnumType.STRING)
    @Column(name = "traffic_type")
    public TrafficType trafficType;


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void setCreate() {
        this.createdAt = LocalDateTime.now();
    }


    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    // Compatibility for callers/tests that use the name "amount" for this field.
    public BigDecimal getAmount() {
        return payed;
    }

    public void setAmount(BigDecimal amount) {
        this.payed = amount;
    }
}
