package com.sid.moviebkg.user.authentication.service;

public interface TokenService {

    String generateToken(String userId, String role);
    String generateRefreshToken(String authHeader);

}
