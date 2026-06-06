package com.qazaq.telecom.subscription;

import com.qazaq.telecom.usagerecords.TrafficType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedTrafficRequest {

    @NotNull
    @Min(0)
    private Integer usedTraffic;

    @NotNull
    private TrafficType trafficType;
}
