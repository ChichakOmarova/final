package com.learn.kidstinyworld.config;

import com.learn.kidstinyworld.security.JwtRequestFilter;
import com.learn.kidstinyworld.service.ParentDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
//
//@Configuration
//@EnableWebSecurity // Security funksiyasini aktiv edir
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtRequestFilter jwtRequestFilter;
//    private final ParentDetailsService parentDetailsService;
//
//    // 1. Password Encoder (Sifrelerin Hashlenmesi ucun)
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 2. Authentication Manager (AuthService-de istifade etdiyimiz komponent)
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(parentDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(authProvider);
//    }
//
//    // 3. Əsas Security Filter Zənciri (Hansı sorğular yoxlanılsın/yoxlanılmasın)
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                // CORS ve CSRF disable (REST API ucun standart)
////                .csrf(csrf -> csrf.disable())
////                .cors(cors -> {}) // CORS ayarlarini application.yml-de qura bilerik
//
//                // Session Management: JWT istifade etdiyimiz ucun stateful yox, stateless qoyuruq
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // Sorğulara giriş qaydaları (Authorization)
//                .authorizeHttpRequests(auth -> auth
//                        // Qeydiyyat ve Login endpoint-leri hamiya aciqdir (filter yoxlamır)
//                        .requestMatchers("/api/auth/**").permitAll()
//
//                        // Actuator endpoint-leri (monitorinq ucun)
//                        .requestMatchers("/actuator/**").permitAll()
//
//                        // Qalan butun sorğular autentifikasiya olunmus istifadeci teleb edir
//                        .anyRequest().authenticated()
//                );
//
//        // JWT Filteri UsernamePasswordAuthenticationFilter-den EVVEL elave et
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final ParentDetailsService parentDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(parentDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/", "/index.html", "/styles.css", "/app.js", "/favicon.ico", "/static/**", "/*.html", "/*.css", "/*.js").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((req, res, ex1) -> res.sendError(403, "Access Denied"))
                        .authenticationEntryPoint((req, res, ex1) -> res.sendError(401, "Unauthorized"))
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
