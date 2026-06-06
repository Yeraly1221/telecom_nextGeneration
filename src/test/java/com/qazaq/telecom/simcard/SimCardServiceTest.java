package com.qazaq.telecom.simcard;

import com.qazaq.telecom.customer.Customer;
import com.qazaq.telecom.customer.CustomerRepository;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.security.access.CurrentCustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimCardServiceTest {

    @Mock
    private SimCardRepository simCardRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CurrentCustomerService currentCustomerService;

    @InjectMocks
    private SimCardService simCardService;

    @Test
    void getSimCardShouldCreateAndSaveSimCard() {
        Customer customer = Customer.builder().id(3L).build();
        GetSimCardRequest request = new GetSimCardRequest("+77001234567");
        when(simCardRepository.existsSimCardByPhoneNumber("+77001234567")).thenReturn(false);
        when(customerRepository.findCustomerById(3L)).thenReturn(Optional.of(customer));
        when(currentCustomerService.requireCustomer(3L)).thenReturn(customer);

        simCardService.getSimCard(3L, request);

        ArgumentCaptor<SimCard> captor = ArgumentCaptor.forClass(SimCard.class);
        verify(simCardRepository).save(captor.capture());
        SimCard simCard = captor.getValue();
        assertEquals("+77001234567", simCard.getPhoneNumber());
        assertSame(customer, simCard.getCustomer());
    }

    @Test
    void getSimCardShouldFailWhenRequestIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> simCardService.getSimCard(3L, null)
        );

        assertEquals("sim card request can not be a null", exception.getMessage());
        verify(simCardRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getSimCardShouldFailWhenNumberAlreadyExists() {
        GetSimCardRequest request = new GetSimCardRequest("+77001234567");
        when(simCardRepository.existsSimCardByPhoneNumber("+77001234567")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> simCardService.getSimCard(3L, request)
        );

        assertEquals("Sim Card already exist", exception.getMessage());
        verify(customerRepository, never()).findCustomerById(3L);
    }
}
