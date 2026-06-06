package com.qazaq.telecom.simcard;


import com.qazaq.telecom.customer.Customer;
import com.qazaq.telecom.customer.CustomerRepository;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.security.access.CurrentCustomerService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class SimCardService {

    public final SimCardRepository simCardRepository;
    public final CustomerRepository customerRepository;
    private final CurrentCustomerService currentCustomerService;

    // Backward-compatible name (some callers/tests use this)
    @Transactional
    public void makeSimCard(Long customer_id, GetSimCardRequest request) {
        createSimCard(customer_id, request);
    }

    @Transactional
    public void getSimCard(Long customer_id, GetSimCardRequest request) {
        createSimCard(customer_id, request);
    }

    @Transactional
    public void  createSimCard(Long customer_id, GetSimCardRequest request){
        if(request == null){
            throw new BusinessException("sim card request can not be a null");
        }

        currentCustomerService.requireCustomer(customer_id);


        if(simCardRepository.existsSimCardByPhoneNumber(request.getPhoneNumber())){
            throw new BusinessException("Sim Card already exist");
        }

        Customer customer = customerRepository.findCustomerById(customer_id)
                .orElseThrow(() -> new BusinessException("Customer noe found"));

        SimCard simCard = SimCard
                .builder()
                .phoneNumber(request.getPhoneNumber())
                .customer(customer)
                .build();

        simCardRepository.save(simCard);
    }


    @Transactional
    public GetPhoneNumberRequest getPhoneNumber(Long id){
        SimCard simCard = simCardRepository.findSimCardById(id)
                .orElseThrow(() -> new BusinessException("Sim card not found"));
        currentCustomerService.ensureSimCardOwner(simCard);

        return GetPhoneNumberRequest.builder()
                .phoneNumber(simCard.getPhoneNumber())
                .build();
    }
}
