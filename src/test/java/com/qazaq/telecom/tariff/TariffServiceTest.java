package com.qazaq.telecom.tariff;

import com.qazaq.telecom.subscription.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private TariffService tariffService;

    @Test
    void addTariffShouldMapRequestToTariffAndSave() {
        Subscription subscription = Subscription.builder().id(2L).build();
        AddTariffRequest request = AddTariffRequest.builder()
                .name("Starter")
                .price(9.99)
                .mbyte(15)
                .minute(120)
                .sms(30)
                .subscription(subscription)
                .build();

        tariffService.addTariff(request);

        ArgumentCaptor<Tariff> captor = ArgumentCaptor.forClass(Tariff.class);
        verify(tariffRepository).save(captor.capture());
        Tariff tariff = captor.getValue();
        assertEquals("Starter", tariff.getName());
        assertEquals(9.99, tariff.getPrice());
        assertEquals(15, tariff.getMegabyteLimit());
        assertEquals(120, tariff.getMinutesLimit());
        assertEquals(30, tariff.getSmsLimit());
        assertEquals(1, tariff.getSubscriptions().size());
        assertSame(subscription, tariff.getSubscriptions().get(0));
    }
}
