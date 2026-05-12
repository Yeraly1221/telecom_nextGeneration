package com.qazaq.telecom.tariff;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TariffService {

    public final TariffRepository tariffRepository;

    public void addTariff(AddTariffRequest request){
        Tariff tariff = Tariff.builder().
                name(request.getName())
                .price(request.getPrice())
                .megabyteLimit(request.getMbyte())
                .minutesLimit(request.getMinute())
                .smsLimit(request.getSms())
                .subscriptions(new ArrayList<>())
                .build();

        if (request.getSubscription() != null) {
            tariff.addSubscription(request.getSubscription());
        }

        tariffRepository.save(tariff);
    }
}
