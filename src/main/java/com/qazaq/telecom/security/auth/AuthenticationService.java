package com.qazaq.telecom.security.auth;

import com.qazaq.telecom.account.Account;
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

@Service
@RequiredArgsConstructor
//var автоматом определяет тип переменной
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;

    public String register(RegisterRequest request){

        if(customerRepository.existsByEmail(request.getEmail())){
            throw new IllegalStateException("Email already exists");
        }

        var user = Customer
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .account(Account.builder()
                        .balance(0.0)
                        .build())
                .role(Role.USER)
                .enabled(false)
                .build();
        customerRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        // отправка письма
        String link = "http://localhost:8080/api/v1/auth/confirm?token=" + jwtToken;
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
        // 1. Извлечь email из токена
        String userEmail = jwtService.extractUsername(token);

        // 2. Найти пользователя
        Customer customer = customerRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Customer not found"));

        // 3. Проверить токен
        if (!jwtService.isTokenValid(token, customer)) {
            throw new IllegalStateException("Token expired or invalid");
        }

        // 4. Проверить не подтверждён ли уже
        if (customer.getEnabled() == true) {
            throw new IllegalStateException("Email already confirmed");
        }

        // 5. Активировать пользователя
        customer.setEnabled(true);
        customerRepository.save(customer);

        // 6. Выдать новый JWT для входа
        String newToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(newToken)
                .message("Email confirmed successfully")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()

                )
        );

        var user = customerRepository.findByEmail(request.getEmail())
                .orElseThrow();


        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Logged in successfully")
                .build();


    }

    private String buildEmail(String name, String link) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Hi " + name + "!</h2>"
                + "<p>Thanks for registering. Please confirm your email:</p>"
                + "<a href='" + link + "' "
                + "style='background-color: #4CAF50; color: white; padding: 14px 20px; "
                + "text-decoration: none; border-radius: 4px;'>Confirm Email</a>"
                + "<p>Link expires in 15 minutes.</p>"
                + "</div>";
    }
}
