package com.bus.ticketingSystem.security;

import com.bus.ticketingSystem.security.filter.AuthenticationFilter;
import com.bus.ticketingSystem.security.filter.ExceptionHandlerFilter;
import com.bus.ticketingSystem.security.filter.JWTAuthorizationFilter;
import com.bus.ticketingSystem.security.manager.CustomAuthenticationManager;
import com.bus.ticketingSystem.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationManager customAuthenticationManager;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager, userService);
        authenticationFilter.setFilterProcessesUrl("/authenticate");

        http
                .cors(Customizer.withDefaults()).csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(SecurityConstants.REGISTER_PATH).permitAll()
                .requestMatchers(HttpMethod.GET, SecurityConstants.BUS_PATH).permitAll()
                .requestMatchers(SecurityConstants.BOOKING_PATH).permitAll()
                .requestMatchers(SecurityConstants.TICKET_PATH).permitAll()
                .requestMatchers(HttpMethod.POST, SecurityConstants.PAYMENT_PATH).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
