package com.qazaq.telecom.subscription;

import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.simcard.SimCard;
import com.qazaq.telecom.simcard.SimCardRepository;
import com.qazaq.telecom.tariff.Tariff;
import com.qazaq.telecom.tariff.TariffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SimCardRepository simCardRepository;

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void createSubscriptionShouldCreateSubscriptionAndLinkEntities() {
        SimCard simCard = SimCard.builder().id(5L).build();
        Tariff tariff = Tariff.builder()
                .id(9L)
                .name("Premium")
                .megabyteLimit(100)
                .minutesLimit(200)
                .smsLimit(50)
                .subscriptions(new ArrayList<>())
                .build();
        when(simCardRepository.findSimCardById(5L)).thenReturn(Optional.of(simCard));
        when(subscriptionRepository.existsSubscriptionsBySimCardId(5L)).thenReturn(false);
        when(tariffRepository.findTariffById(9L)).thenReturn(Optional.of(tariff));

        subscriptionService.createSubscription(5L, 9L);

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());
        Subscription savedSubscription = subscriptionCaptor.getValue();
        assertEquals("Premium", savedSubscription.getTariffName());
        assertEquals(100, savedSubscription.getMegabyte());
        assertEquals(200, savedSubscription.getMinutes());
        assertEquals(50, savedSubscription.getSms());
        assertEquals(SubscriptionStatus.ACTIVE, savedSubscription.getSubscriptionStatus());
        assertNotNull(savedSubscription.getStartDate());
        assertNotNull(savedSubscription.getEndDate());
        assertSame(simCard, savedSubscription.getSimCard());
        assertSame(tariff, savedSubscription.getTariff());

        verify(tariffRepository).save(tariff);
        verify(simCardRepository).save(simCard);
        assertSame(savedSubscription, simCard.getSubscription());
        assertEquals(1, tariff.getSubscriptions().size());
        assertSame(savedSubscription, tariff.getSubscriptions().get(0));
    }

    @Test
    void changeTariffShouldRefreshSubscriptionFromTariff() {
        LocalDateTime oldStart = LocalDateTime.now().minusMonths(2);
        LocalDateTime oldEnd = LocalDateTime.now().minusMonths(1);
        Tariff tariff = Tariff.builder()
                .id(11L)
                .name("Max")
                .megabyteLimit(500)
                .minutesLimit(300)
                .smsLimit(150)
                .subscriptions(new ArrayList<>())
                .build();
        Subscription subscription = Subscription.builder()
                .id(4L)
                .tariffName("Old")
                .megabyte(10)
                .minutes(20)
                .sms(5)
                .startDate(oldStart)
                .endDate(oldEnd)
                .build();
        when(tariffRepository.findTariffById(11L)).thenReturn(Optional.of(tariff));
        when(subscriptionRepository.findSubscriptionById(4L)).thenReturn(Optional.of(subscription));

        subscriptionService.changeTariff(4L, 11L);

        verify(subscriptionRepository).save(subscription);
        verify(tariffRepository).save(tariff);
        assertSame(tariff, subscription.getTariff());
        assertEquals("Max", subscription.getTariffName());
        assertEquals(500, subscription.getMegabyte());
        assertEquals(300, subscription.getMinutes());
        assertEquals(150, subscription.getSms());
        assertNotNull(subscription.getStartDate());
        assertNotNull(subscription.getEndDate());
        assertSame(subscription, tariff.getSubscriptions().get(0));
    }

    @Test
    void getRemainingShouldMapSubscriptionToResponse() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(28);
        Subscription subscription = Subscription.builder()
                .id(8L)
                .tariffName("Lite")
                .megabyte(40)
                .minutes(60)
                .sms(20)
                .startDate(start)
                .endDate(end)
                .build();
        when(subscriptionRepository.findSubscriptionById(8L)).thenReturn(Optional.of(subscription));

        CurrentRemainRequest result = subscriptionService.getRemaining(8L);

        assertEquals("Lite", result.getTariffName());
        assertEquals(40, result.getMegabyte());
        assertEquals(60, result.getMinutes());
        assertEquals(20, result.getSms());
        assertEquals(start, result.getStartDate());
        assertEquals(end, result.getEndDate());
    }

    @Test
    void updateTariffShouldResetRemainingValuesFromCurrentTariff() {
        LocalDateTime oldStart = LocalDateTime.now().minusMonths(3);
        LocalDateTime oldEnd = LocalDateTime.now().minusMonths(2);
        Tariff tariff = Tariff.builder()
                .name("Business")
                .megabyteLimit(1000)
                .minutesLimit(700)
                .smsLimit(250)
                .build();
        Subscription subscription = Subscription.builder()
                .id(12L)
                .tariff(tariff)
                .megabyte(1)
                .minutes(2)
                .sms(3)
                .startDate(oldStart)
                .endDate(oldEnd)
                .build();
        when(subscriptionRepository.findSubscriptionById(12L)).thenReturn(Optional.of(subscription));

        subscriptionService.updateTariff(12L);

        verify(subscriptionRepository).save(subscription);
        assertEquals(1000, subscription.getMegabyte());
        assertEquals(700, subscription.getMinutes());
        assertEquals(250, subscription.getSms());
        assertNotNull(subscription.getStartDate());
        assertNotNull(subscription.getEndDate());
    }

    @Test
    void getRemainingShouldFailWhenSubscriptionIsMissing() {
        when(subscriptionRepository.findSubscriptionById(99L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> subscriptionService.getRemaining(99L)
        );

        assertEquals("Subscription not found", exception.getMessage());
    }
}
