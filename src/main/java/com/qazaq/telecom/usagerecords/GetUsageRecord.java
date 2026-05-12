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

    private TrafficType trafficType;
}
