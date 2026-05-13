package com.qazaq.telecom.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/registration/{id}")
    public GetCustomerRequest GetCustomerInfo(@PathVariable Long id){
       return customerService.getInfoAboutCustomer(id);
    }

}
