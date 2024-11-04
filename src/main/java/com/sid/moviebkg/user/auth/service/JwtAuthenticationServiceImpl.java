package com.sid.moviebkg.user.auth.service;

import com.sid.moviebkg.common.token.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationServiceImpl {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    private final JwtService jwtService;

    public UsernamePasswordAuthenticationToken authenticate(String authHeader) {
        UsernamePasswordAuthenticationToken authToken = null;
        final String jwt = authHeader.substring(7);
        final String userId = jwtService.extractUsername(jwt, secretKey);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null && jwtService.isTokenValid(jwt, secretKey)) {
            Claims claims = jwtService.extractAllClaims(jwt, secretKey);
            String roles = claims.get("role", String.class);
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (StringUtils.hasText(roles)) {
                authorities = Arrays.stream(roles.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            UserDetails userDetails = new User(userId, userId, authorities);
            authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
        }
        return authToken;
    }
}
