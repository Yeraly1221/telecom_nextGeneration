package com.qazaq.telecom.usagerecords;

import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.subscription.Subscription;
import com.qazaq.telecom.subscription.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageRecordService {

    public final SubscriptionRepository subscriptionRepository;
    public final UsageRecordsRepository usageRecordsRepository;

    @Transactional
    public void createUsageRecord(Long subscription_id, GetUsageRecord getUsageRecord){

        Subscription subscription = subscriptionRepository.findSubscriptionById(subscription_id)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

        UsageRecords usageRecords = UsageRecords.builder()
                .usedTraffic(getUsageRecord.getUsedTraffic())
                .payed(getUsageRecord.getPayed())
                .trafficType(getUsageRecord.getTrafficType())
                .subscription(subscription)
                .build();
        usageRecordsRepository.save(usageRecords);


        subscription.addUsageRecord(usageRecords);
        subscriptionRepository.save(subscription);
    }

    public void saveUsageRecords(UsageRecords usageRecords){
        usageRecordsRepository.save(usageRecords);
    }



}
