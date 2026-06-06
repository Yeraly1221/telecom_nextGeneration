package com.qazaq.telecom.subscription;


import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.simcard.SimCard;
import com.qazaq.telecom.simcard.SimCardRepository;
import com.qazaq.telecom.security.access.CurrentCustomerService;
import com.qazaq.telecom.tariff.Tariff;
import com.qazaq.telecom.tariff.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {


    public final SubscriptionRepository subscriptionRepository;
    public final SimCardRepository simCardRepository;
    public final TariffRepository tariffRepository;
    private final CurrentCustomerService currentCustomerService;


    @Transactional
    public void createSubscription(Long simCardId, Long tariffId){
        SimCard simCard = simCardRepository.findSimCardById(simCardId)
                .orElseThrow(() -> new BusinessException("Customer not found"));
        currentCustomerService.ensureSimCardOwner(simCard);

        if(subscriptionRepository.existsSubscriptionsBySimCardId(simCardId)){
            throw new BusinessException("Subscription already exist");
        }

        Tariff tariff = tariffRepository.findTariffById(tariffId)
                .orElseThrow(() -> new BusinessException("Tariff not found"));


        Subscription subscription = Subscription.builder()
                .tariffName(tariff.getName())
                .megabyte(tariff.getMegabyteLimit())
                .sms(tariff.getSmsLimit())
                .minutes(tariff.getMinutesLimit())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .tariff(tariff)
                .build();
        subscriptionRepository.save(subscription);


        tariff.addSubscription(subscription);
        tariffRepository.save(tariff);

        // Owning side is SimCard.subscription (Subscription.simCard is mappedBy)
        simCard.setSubscription(subscription);
        subscription.setSimCard(simCard);
        simCardRepository.save(simCard);
    }


    @Transactional
    public void changeTariff(Long subscriptionId, Long tariffId){

        Tariff tariff = tariffRepository.findTariffById(tariffId)
                .orElseThrow(() -> new BusinessException("Tariff not found"));

        Subscription subscription = subscriptionRepository.findSubscriptionById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));
        currentCustomerService.ensureSubscriptionOwner(subscription);

        Tariff previousTariff = subscription.getTariff();

        subscription.setTariff(tariff);
        subscription.setMegabyte(tariff.getMegabyteLimit());
        subscription.setSms(tariff.getSmsLimit());
        subscription.setMinutes(tariff.getMinutesLimit());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription.setTariffName(tariff.getName());
        subscriptionRepository.save(subscription);

        if (previousTariff != null && !previousTariff.getId().equals(tariff.getId())) {
            previousTariff.getSubscriptions().remove(subscription);
            tariffRepository.save(previousTariff);
        }
        tariff.addSubscription(subscription);
        tariffRepository.save(tariff);

    }


    @Transactional
    public CurrentRemainRequest getRemaining(Long subscriptionId){

        Subscription subscription = subscriptionRepository.findSubscriptionById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));
        currentCustomerService.ensureSubscriptionOwner(subscription);

        
       return CurrentRemainRequest.builder()
               .tariffName(subscription.getTariffName())
               .megabyte(subscription.getMegabyte())
               .sms(subscription.getSms())
               .minutes(subscription.getMinutes())
               .startDate(subscription.getStartDate())
               .endDate(subscription.getEndDate())
               .build();
    }


    @Transactional
    public void updateTariff(Long subscriptionId){

        Subscription subscription = subscriptionRepository.findSubscriptionById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));
        currentCustomerService.ensureSubscriptionOwner(subscription);

        Tariff tariff = subscription.getTariff();

        subscription.setMegabyte(tariff.getMegabyteLimit());
        subscription.setSms(tariff.getSmsLimit());
        subscription.setMinutes(tariff.getMinutesLimit());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription.setTariffName(tariff.getName());
        subscriptionRepository.save(subscription);

    }

    public Subscription getSubscription(Long subscriptionId){
        return subscriptionRepository.findSubscriptionById(subscriptionId)
                .orElseThrow(() -> new BusinessException("Subscription not found"));

    }

    public void saveSubscription(Subscription subscription){
        subscriptionRepository.save(subscription);
    }




}
