package com.sid.moviebkg.user.auth.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

public interface AuthenticationService {
    UsernamePasswordAuthenticationToken authenticate(String authHeader);
    default UsernamePasswordAuthenticationToken getAuthToken(String userId) {
        UserDetails userDetails = new User(userId, userId, new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
