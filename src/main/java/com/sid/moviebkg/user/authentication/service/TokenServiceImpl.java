package com.sid.moviebkg.user.authentication.service;

import com.sid.moviebkg.common.token.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String generateToken(String userId, String role) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        UserDetails userDetails = new User(userId, userId, authorities);
        return jwtService.generateToken(secretKey, userDetails, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(String authHeader) {
        final String token = authHeader.substring(7);
        return jwtService.generateRefreshToken(token, secretKey, jwtExpiration);
    }
}
