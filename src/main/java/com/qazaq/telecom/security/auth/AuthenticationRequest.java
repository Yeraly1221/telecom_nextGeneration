package com.qazaq.telecom.security.auth;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data // это анотация включает getter setter equals
@Builder // удобный способ создавать обекты типо bulder() после .name("Yeraly")
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
