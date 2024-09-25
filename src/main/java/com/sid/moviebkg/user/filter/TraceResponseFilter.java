package com.sid.moviebkg.user.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.sid.moviebkg.user.util.UserCmnConstants.TRACEID;
import static com.sid.moviebkg.user.util.UserCmnConstants.TRACE_ID;

@Component
@Order(3)
public class TraceResponseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        String traceId = getTraceId();
        if (StringUtils.hasText(traceId)) {
            response.addHeader(TRACEID, traceId);
        }
    }

    String getTraceId() {
        String traceId = null;
        if (MDC.getCopyOfContextMap().containsKey(TRACE_ID)) {
            traceId = MDC.get(TRACE_ID);
        } else if (MDC.getCopyOfContextMap().containsKey(TRACEID)) {
            traceId = MDC.get(TRACEID);
        }
        return traceId;
    }
}
