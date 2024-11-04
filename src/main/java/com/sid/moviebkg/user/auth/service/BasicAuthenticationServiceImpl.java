package com.sid.moviebkg.user.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.sid.moviebkg.user.util.UserCmnConstants.COLON;
import static org.springframework.security.web.authentication.www.BasicAuthenticationConverter.AUTHENTICATION_SCHEME_BASIC;
import static org.springframework.util.StringUtils.hasText;

@Service
public class BasicAuthenticationServiceImpl implements AuthenticationService {
    @Value("${spring.security.user.name:#{null}}")
    private String userName;
    @Value("${spring.security.user.password:#{null}}")
    private String password;

    @Override
    public UsernamePasswordAuthenticationToken authenticate(String authHeader) {
        authHeader = authHeader.trim();
        if (authHeader.equalsIgnoreCase(AUTHENTICATION_SCHEME_BASIC)) {
            throw new BadCredentialsException("Empty basic authentication token");
        }
        UsernamePasswordAuthenticationToken authToken = null;
        Charset charset = StandardCharsets.UTF_8;
        byte[] base64Token = authHeader.substring(6).getBytes(charset);
        byte[] decoded = decode(base64Token);
        String token = new String(decoded, charset);
        int delim = token.indexOf(COLON);
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        String lgnUsr = token.substring(0, delim);
        String lgnPwd = token.substring(delim + 1);
        if (hasText(lgnUsr) && hasText(lgnPwd) && hasText(userName) && hasText(password) && userName.equals(lgnUsr) &&
                password.equals(lgnPwd)) {
            authToken = getAuthToken(lgnUsr);
        }
        return authToken;
    }

    private byte[] decode(byte[] base64Token) {
        try {
            return Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
    }
}
