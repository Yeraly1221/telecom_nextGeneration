package com.qazaq.telecom.subscription;


import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.usagerecords.TrafficType;
import com.qazaq.telecom.usagerecords.UsageRecordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UseTrafficUseCase {

    public final SubscriptionService subscriptionService;
    public final UsageRecordService usageRecordService;

    @Transactional
    public void useTraffic(Long subscriptionId, UsedTrafficRequest usedTrafficRequest) {
        Subscription subscription = subscriptionService.getSubscription(subscriptionId);

        if (usedTrafficRequest.getUsedTraffic() < 0.0) {
            throw new BusinessException("Used traffic can not be lover then 0.0");
        }

        Integer currentTraffic = 0;
        Integer trafficPrice = 0;
        Integer customerPayed = 0;
        switch (usedTrafficRequest.getTrafficType()) {
            case MEGABYTE -> {
                currentTraffic = subscription.getMegabyte();
                trafficPrice = 20;
            }

            case SMS -> {
                currentTraffic = subscription.getSms();
                trafficPrice = 50;
            }

            case MINUTES -> {
                currentTraffic = subscription.getMinutes();
                trafficPrice = 35;
            }
        }

        if (currentTraffic - usedTrafficRequest.getUsedTraffic() < 0) {
            currentTraffic = 0;
            customerPayed = (usedTrafficRequest.getUsedTraffic() - currentTraffic) * trafficPrice;
        }else{
            currentTraffic -= usedTrafficRequest.getUsedTraffic();

        }
    }




}
