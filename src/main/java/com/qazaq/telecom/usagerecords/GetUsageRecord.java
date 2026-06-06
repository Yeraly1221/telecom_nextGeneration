package com.qazaq.telecom.usagerecords;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsageRecord {

    @NotNull
    @Min(0)
    private Integer usedTraffic;

    private BigDecimal payed;

    // Compatibility with older naming; maps to the same concept as "payed"
    private BigDecimal amount;

    private TrafficType trafficType;
}
