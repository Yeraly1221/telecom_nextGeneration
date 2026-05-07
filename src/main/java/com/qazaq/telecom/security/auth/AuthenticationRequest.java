package com.qazaq.telecom.security.auth;

import lombok.*;

@Data // это анотация включает getter setter equals
@Builder // удобный способ создавать обекты типо bulder() после .name("Yeraly")
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    private String email;

    private String password;
}
