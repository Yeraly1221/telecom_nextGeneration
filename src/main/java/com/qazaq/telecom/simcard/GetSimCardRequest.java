package com.qazaq.telecom.simcard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSimCardRequest {
    private String phoneNumber;
}
