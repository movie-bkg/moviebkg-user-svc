package com.sid.moviebkg.user.filter;

import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.common.request.HttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private MBkgLogger log = MBkgLoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        logRequest(requestWrapper);
        filterChain.doFilter(requestWrapper, responseWrapper);
        logResponse(responseWrapper);
    }

    private void logRequest(HttpServletRequestWrapper request) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append(request.getMethod()).append(' ');
        msg.append(request.getRequestURI());
        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            msg.append('?').append(queryString);
        }
        String payload = request.readInputAndDuplicate();
        if (StringUtils.hasText(payload)) {
            msg.append(", payload=").append(payload.replace("\r\n", "").replace("\n", ""));
        }
        log.info("Request:{}", msg.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        log.info("Response:{}", new String(response.getContentAsByteArray()));
        response.copyBodyToResponse();
    }
}
