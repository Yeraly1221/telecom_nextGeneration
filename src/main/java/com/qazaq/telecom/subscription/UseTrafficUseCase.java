package com.qazaq.telecom.subscription;


import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.account.AccountRepository;
import com.qazaq.telecom.account.AccountService;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.payment.PaymentRequest;
import com.qazaq.telecom.payment.PaymentService;
import com.qazaq.telecom.payment.PaymentType;
import com.qazaq.telecom.usagerecords.UsageRecordService;
import com.qazaq.telecom.usagerecords.UsageRecords;
import com.qazaq.telecom.usagerecords.UsageRecordsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UseTrafficUseCase {

    public final SubscriptionService subscriptionService;
    public final UsageRecordService usageRecordService;
    public final AccountService accountService;
    private final UsageRecordsRepository usageRecordsRepository;

    @Transactional
    public void useTraffic(Long subscriptionId, UsedTrafficRequest usedTrafficRequest) {
        if (usedTrafficRequest == null || usedTrafficRequest.getUsedTraffic() == null || usedTrafficRequest.getTrafficType() == null) {
            throw new BusinessException("Traffic usage request is required");
        }
        Subscription subscription = subscriptionService.getSubscription(subscriptionId);

        if (usedTrafficRequest.getUsedTraffic() < 0) {
            throw new BusinessException("Used traffic can not be lover then 0.0");
        }

        Integer currentTraffic = 0;
        BigDecimal trafficPrice = BigDecimal.ZERO;
        Integer usedTraffic = usedTrafficRequest.getUsedTraffic();
        BigDecimal payed = BigDecimal.ZERO;

        switch (usedTrafficRequest.getTrafficType()) {
            case MEGABYTE -> { currentTraffic = subscription.getMegabyte(); trafficPrice = BigDecimal.valueOf(20.0); }
            case SMS      -> { currentTraffic = subscription.getSms();      trafficPrice = BigDecimal.valueOf(50.0); }
            case MINUTES  -> { currentTraffic = subscription.getMinutes();  trafficPrice = BigDecimal.valueOf(35.0); }
            default -> throw new BusinessException("Unknown traffic type");
        }

        if(currentTraffic < usedTraffic){
            Integer overage = usedTraffic - currentTraffic;
            currentTraffic = 0;
            payed = trafficPrice.multiply(BigDecimal.valueOf(overage));
            Account account = subscription.getSimCard().getCustomer().getAccount();
            if(account.getBalance().compareTo(payed) < 0){
                throw new BusinessException("Customer does not have enough money");
            }
            accountService.withDrawBalance(account.getId(),
                    PaymentRequest.builder()
                            .paymentType(PaymentType.ONLINE)
                            .amount(payed)
                            .build());
        }else {
            payed = BigDecimal.ZERO;
            currentTraffic -= usedTraffic;
        }

        UsageRecords usageRecords = UsageRecords.builder()
                .usedTraffic(usedTraffic)
                .trafficType(usedTrafficRequest.getTrafficType())
                .payed(payed)
                .subscription(subscription)
                .build();
        usageRecordsRepository.save(usageRecords);

        switch (usedTrafficRequest.getTrafficType()) {
            case MEGABYTE -> { subscription.setMegabyte(currentTraffic); }
            case SMS      -> { subscription.setSms(currentTraffic); }
            case MINUTES  -> { subscription.setMinutes(currentTraffic); }
        }

        subscriptionService.saveSubscription(subscription);


    }




}
