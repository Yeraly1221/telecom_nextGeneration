package com.qazaq.telecom.simcard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSimCardRequest {
    @NotBlank
    private String phoneNumber;
}
