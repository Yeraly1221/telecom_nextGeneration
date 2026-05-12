package com.qazaq.telecom.security.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/*
application:
  security:
    jwt:
      secret-key: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
      expiration: 900000 # 15 минут
      это конфигурация релаизуе другую конфигурацию
      и будем импользовать ее как
      // Вариант 1 — через @Value (без JwtProperties)
@Value("${application.security.jwt.secret-key}")
private String secretKey;

// Вариант 2 — через JwtProperties класс
private final JwtProperties jwtProperties;
jwtProperties.getSecretKey();
 */
@ConfigurationProperties(prefix = "application.security.jwt")
@Configuration
@Data
public class JwtProperties {
    private String secretKey;
    private long expiration;
}
