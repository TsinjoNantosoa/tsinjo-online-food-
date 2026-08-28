package com.tsinjo.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import com.tsinjo.service.CustomerUserDetailsService;

import java.io.IOException;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidator.class);
    private final JwtProvider jwtProvider;
    private final CustomerUserDetailsService userDetailsService;

    public JwtTokenValidator(JwtProvider jwtProvider, CustomerUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(JwtConstant.JWT_HEADER);
        if (header != null && header.startsWith(JwtConstant.BEARER_PREFIX)) {
            try {
                Claims claims = jwtProvider.parseClaims(header);
                String email = claims.getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities()));
            } catch (Exception exception) {
                SecurityContextHolder.clearContext();
                log.debug("Rejected invalid JWT for {}", request.getRequestURI());
            }
        }
        filterChain.doFilter(request, response);
    }
}
