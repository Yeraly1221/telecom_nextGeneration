package com.qazaq.telecom.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomerRequest {


    private String firstName;


    private String lastName;

    private String phoneNumber;


    private String email;


    private BigDecimal balance;
}
