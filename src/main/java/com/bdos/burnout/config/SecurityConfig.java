//package com.bdos.burnout.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // CSRF ko disable kar rahe hain taaki login/register form smoothly submit ho sakein
//                .csrf(csrf -> csrf.disable())
//
//                // Sabhi URLs ko publicly accessible (permAll) bana rahe hain taaki security kisi page ko block na kare
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/**", "/css/**", "/js/**", "/assets/**", "/components/**").permitAll()
//                        .anyRequest().permitAll()
//                )
//
//                // Default Form Login aur Logout configurations
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}