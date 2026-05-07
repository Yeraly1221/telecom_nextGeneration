package com.qazaq.telecom.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
/*@EnableWebSecurity — это аннотация которая
 активирует Spring Security в твоём приложении.
 */
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .csrf(csrf -> csrf.disable())
                //Зашита о межсайтовых запросов отключаем
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**")
                        .permitAll()
                        .anyRequest().authenticated()
                        //тут мы даем дабро все кто кидает запрос с url /api/public/**
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        //Это уже из RestApi Он не помнит запросы и не хранит их в памяти
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                // поставил Jwt филтер до стандартного

        return http.build();
    }
}
