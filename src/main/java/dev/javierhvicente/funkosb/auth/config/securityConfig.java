package dev.javierhvicente.funkosb.auth.config;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class securityConfig {
    private final UserDetailsService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public securityConfig(UserDetailsService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(request -> request.requestMatchers("/error/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers("/ws/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.PUT,"funkos/v1/funkos/**").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.DELETE,"funkos/v1/funkos/**").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET,"funkos/v1/funkos/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET,"funkos/v1/categorias/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST,"funkos/v1/auth/**").permitAll())
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST,"funkos/v1/categorias").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.PUT,"funkos/v1/categorias/**").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.DELETE,"funkos/v1/categorias/**").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST,"funkos/v1/pedidos").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.PUT,"funkos/v1/pedidos/**").hasRole("ADMIN"))
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.GET,"funkos/v1/storage/**").authenticated())
                .authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST,"funkos/v1/storage").authenticated())
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

