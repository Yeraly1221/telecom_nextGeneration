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

@Service
@RequiredArgsConstructor
public class UseTrafficUseCase {

    public final SubscriptionService subscriptionService;
    public final UsageRecordService usageRecordService;
    public final AccountService accountService;
    private final UsageRecordsRepository usageRecordsRepository;

    @Transactional
    public void useTraffic(Long subscriptionId, UsedTrafficRequest usedTrafficRequest) {
        Subscription subscription = subscriptionService.getSubscription(subscriptionId);

        if (usedTrafficRequest.getUsedTraffic() < 0.0) {
            throw new BusinessException("Used traffic can not be lover then 0.0");
        }

        Integer currentTraffic = 0;
        Double trafficPrice = 0.0;
        Integer usedTraffic = usedTrafficRequest.getUsedTraffic();
        Double payed = 0.0;

        switch (usedTrafficRequest.getTrafficType()) {
            case MEGABYTE -> { currentTraffic = subscription.getMegabyte(); trafficPrice = 20.0; }
            case SMS      -> { currentTraffic = subscription.getSms();      trafficPrice = 50.0; }
            case MINUTES  -> { currentTraffic = subscription.getMinutes();  trafficPrice = 35.0; }
            default -> throw new BusinessException("Unknown traffic type");
        }

        if(currentTraffic < usedTraffic){
            currentTraffic = 0;
            payed = (usedTraffic - currentTraffic) * trafficPrice ;
            Account account = subscription.getSimCard().getCustomer().getAccount();
            if(account.getBalance() < payed){
                throw new BusinessException("Customer does not have enough money");
            }
            accountService.withDrawBalance(account.getId(),
                    PaymentRequest.builder()
                            .paymentType(PaymentType.ONLINE)
                            .amount(payed)
                            .build());
        }else {
            payed = 0.0;
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
