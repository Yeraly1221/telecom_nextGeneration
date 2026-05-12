package com.qazaq.telecom.customer;


import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.security.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    public final CustomerRepository customerRepository;


    public GetCustomerRequest getInfoAboutCustomer(Long customer_id){

        Customer customer = customerRepository.findCustomerById(customer_id)
                .orElseThrow(() -> new BusinessException("Customer not found"));
        Account account = customer.getAccount();


        return GetCustomerRequest.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .balance(account.getBalance())
                .build();
    }


}
