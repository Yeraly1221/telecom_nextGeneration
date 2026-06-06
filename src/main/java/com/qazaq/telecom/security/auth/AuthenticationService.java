package com.qazaq.telecom.security.auth;

import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.exception.BusinessException;
import com.qazaq.telecom.security.configuration.JwtService;
import com.qazaq.telecom.security.email.EmailSender;
import com.qazaq.telecom.customer.Customer;
import com.qazaq.telecom.customer.Role;
import com.qazaq.telecom.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
//var автоматом определяет тип переменной
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;

    @Value("${application.base-url}")
    private String baseUrl;

    public String register(RegisterRequest request){
        if (request == null) {
            throw new BusinessException("Registration request is required");
        }
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new BusinessException("Email and password are required");
        }

        if(customerRepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Email already exists");
        }

        var user = Customer
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .account(Account.builder()
                        .balance(BigDecimal.ZERO)
                        .build())
                .role(Role.USER)
                .enabled(false)
                .build();
        customerRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        // отправка письма
        String link = baseUrl + "/api/v1/auth/confirm?token=" + jwtToken;
        emailSender.send(
                request.getEmail(),
                buildEmail(request.getFirstName(), link)
        );

        return "Check your email to confirm registration";


        /*return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

         */
    }

    public AuthenticationResponse confirm(String token) {
        final String userEmail;
        try {
            userEmail = jwtService.extractUsername(token);
        } catch (Exception ex) {
            throw new BusinessException("Token expired or invalid");
        }

        Customer customer = customerRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Customer not found"));

        if (!jwtService.isTokenValid(token, customer)) {
            throw new BusinessException("Token expired or invalid");
        }

        if (Boolean.TRUE.equals(customer.getEnabled())) {
            throw new BusinessException("Email already confirmed");
        }

        customer.setEnabled(true);
        customerRepository.save(customer);

        String newToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(newToken)
                .message("Email confirmed successfully")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        if (request == null) {
            throw new BusinessException("Login request is required");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()

                )
        );

        var user = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Customer not found"));


        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Logged in successfully")
                .build();


    }

    private String buildEmail(String name, String link) {
        String safeName = HtmlUtils.htmlEscape(name == null ? "" : name);
        return "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Hi " + safeName + "!</h2>"
                + "<p>Thanks for registering. Please confirm your email:</p>"
                + "<a href='" + link + "' "
                + "style='background-color: #4CAF50; color: white; padding: 14px 20px; "
                + "text-decoration: none; border-radius: 4px;'>Confirm Email</a>"
                + "<p>Link expires in 15 minutes.</p>"
                + "</div>";
    }
}
