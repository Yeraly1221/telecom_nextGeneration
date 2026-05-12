package com.qazaq.telecom.subscription;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentRemainRequest {

    private String tariffName;

    private Integer megabyte;

    private Integer sms;

    private Integer minutes;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
