package com.loctran.store.filters;

import com.loctran.store.entities.Jwt;
import com.loctran.store.entities.Role;
import com.loctran.store.services.JwtService;
import com.loctran.store.services.UserUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserUserDetailsService userUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Jwt jwt = jwtService.parseToken(token);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwt.getUserIdFromToken();
        String email = jwt.getEmailFromToken();
        Role role = jwt.getRoleFromToken();

        if ( userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userUserDetailsService.loadUserByUsername(email);

            if( jwt.isTokenValid(userId) ){
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken
                                (userDetails, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
