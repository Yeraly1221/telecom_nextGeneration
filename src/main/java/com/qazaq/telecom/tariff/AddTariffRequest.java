package com.qazaq.telecom.tariff;

import com.qazaq.telecom.subscription.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTariffRequest {
    private String name;

    private Double price;

    private Integer mbyte;

    private Integer minute;

    private Integer sms;

    private Subscription subscription;
}
