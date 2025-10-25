package com.learn.kidstinyworld.security;

import com.learn.kidstinyworld.service.ParentDetailsService;
import com.learn.kidstinyworld.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// Her sorğu üçün yalnız bir dəfə işə düşən filter
public class JwtRequestFilter extends OncePerRequestFilter {

    // JwtUtil ve ParentDetailsService-i DI vasitesi ile daxil edirik
    private final ParentDetailsService parentDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 1. JWT-ni 'Bearer ' prefiksi ile header-den çıxar
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token tehlilinde sehv (Məs: Token expired)
                logger.warn("JWT Tokeni xetalidir veya vaxti kecib: " + e.getMessage());
            }
        }

        // 2. İstifadəçi adı varmi ve hal-hazirda autentifikasiya olunmayibmi?
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.parentDetailsService.loadUserByUsername(username);

            // 3. Tokeni tekrar yoxla
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 4. Autentifikasiyani El ile Qur
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Security Context-e otur
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}