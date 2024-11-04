package com.sid.moviebkg.user.filter;

import com.sid.moviebkg.common.token.service.JwtService;
import com.sid.moviebkg.user.auth.service.BasicAuthenticationServiceImpl;
import com.sid.moviebkg.user.auth.service.JwtAuthenticationServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sid.moviebkg.user.util.UserCmnConstants.*;
import static org.springframework.security.web.authentication.www.BasicAuthenticationConverter.AUTHENTICATION_SCHEME_BASIC;


@Component
@Order(2)
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${token.disabled:#{null}}")
    private Boolean tokenDisabled;
    private final BasicAuthenticationServiceImpl basicAuthenticationService;
    private final JwtAuthenticationServiceImpl jwtAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (tokenDisabled != null && BooleanUtils.isTrue(tokenDisabled)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getServletPath().contains(LOGIN) || request.getServletPath().contains(REGISTER)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader(AUTHORIZATION);
        boolean isBasicAuth = authHeader != null && authHeader.startsWith(AUTHENTICATION_SCHEME_BASIC);
        boolean isJwtAuth = authHeader != null && authHeader.startsWith(BEARER);
        if (request.getServletPath().contains(SERVLET_PATH_USER_DETAILS) && isBasicAuth) {
            UsernamePasswordAuthenticationToken token = basicAuthenticationService.authenticate(authHeader);
            setAuthToken(request, token);
            filterChain.doFilter(request, response);
            return;
        }
        if (isJwtAuth) {
            UsernamePasswordAuthenticationToken token = jwtAuthenticationService.authenticate(authHeader);
            setAuthToken(request, token);
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthToken(HttpServletRequest request, UsernamePasswordAuthenticationToken token) {
        if (token != null) {
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }
}
