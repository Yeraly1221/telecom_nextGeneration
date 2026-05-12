package com.qazaq.telecom.usagerecords;

import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.subscription.Subscription;
import com.qazaq.telecom.subscription.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageRecordServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UsageRecordsRepository usageRecordsRepository;

    @InjectMocks
    private UsageRecordService usageRecordService;

    @Test
    void createUsageRecordShouldSaveRecordAndAttachItToSubscription() {
        Subscription subscription = Subscription.builder()
                .id(13L)
                .usageRecords(new ArrayList<>())
                .build();
        GetUsageRecord request = GetUsageRecord.builder()
                .amount(2.5)
                .trafficType(TrafficType.MEGABYTE)
                .build();
        when(subscriptionRepository.findSubscriptionById(13L)).thenReturn(Optional.of(subscription));

        usageRecordService.createUsageRecord(13L, request);

        ArgumentCaptor<UsageRecords> captor = ArgumentCaptor.forClass(UsageRecords.class);
        verify(usageRecordsRepository).save(captor.capture());
        UsageRecords usageRecord = captor.getValue();
        assertEquals(2.5, usageRecord.getAmount());
        assertEquals(TrafficType.MEGABYTE, usageRecord.getTrafficType());
        assertSame(subscription, usageRecord.getSubscription());

        verify(subscriptionRepository).save(subscription);
        assertEquals(1, subscription.getUsageRecords().size());
        assertSame(usageRecord, subscription.getUsageRecords().get(0));
    }

    @Test
    void createUsageRecordShouldFailWhenSubscriptionDoesNotExist() {
        GetUsageRecord request = GetUsageRecord.builder()
                .amount(1.0)
                .trafficType(TrafficType.SMS)
                .build();
        when(subscriptionRepository.findSubscriptionById(13L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> usageRecordService.createUsageRecord(13L, request)
        );

        assertEquals("Subscription not found", exception.getMessage());
    }
}
