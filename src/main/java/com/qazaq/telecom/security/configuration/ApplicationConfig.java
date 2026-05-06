package com.qazaq.telecom.security.configuration;

import com.qazaq.telecom.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    /*
    Это у нас функциональный интерфейс который работает с методом loadUserByName
    и бросвет исключение если user не найдей
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
    когда мы вызваем PasswordEncoder то передает BCryptPasswordEncoder это дает
    возможность гибкому маштабированиему потомучто мы в любой момент можем изменить
    возврашяеммый элемент PasswordEncoder
     */

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
    /*
    Тут у нас класс DaoAuthenticationProvider он принимает обекты и после передается как
     AuthenticationProvider это дает возможность скрыть от spring сам класс DaoAuthenticationProvider
     но использовать их обшии переопределенный метод
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
        return authenticationConfiguration.getAuthenticationManager();
    }
    /*
    это функциональный интерфейс импплементирует класс ProviderManager у него под сылкой интерфейса
    находится это кторый в свою очерерть хранит профайдеры типа DaoAuthenticationProvider и когда время использует
    // спрашивает каждого — ты умеешь это обработать?
            if (provider.supports(authentication.getClass())) {

                // нашёл нужный — передаёт ему
                return provider.authenticate(authentication);
                //               ↑
                //               вот здесь работает твой DaoAuthenticationProvider
            }


     */
}

