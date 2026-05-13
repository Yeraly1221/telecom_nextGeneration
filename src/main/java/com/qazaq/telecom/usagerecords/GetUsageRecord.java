package com.qazaq.telecom.usagerecords;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsageRecord {

    private Integer usedTraffic;

    private Double payed;

    // Compatibility with older naming; maps to the same concept as "payed"
    private Double amount;

    private TrafficType trafficType;
}
