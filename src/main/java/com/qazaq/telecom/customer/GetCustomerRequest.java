package com.qazaq.telecom.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomerRequest {


    private String firstName;


    private String lastName;


    private String email;


    private Double balance;
}
