package com.qazaq.telecom.subscription;

import com.qazaq.telecom.usagerecords.TrafficType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedTrafficRequest {


    private Integer usedTraffic;


    private TrafficType trafficType;
}
