package com.sid.moviebkg.user.authentication.routes;

import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.common.utils.StopWatch;
import com.sid.moviebkg.user.authentication.service.AuthenticationResponseHandler;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationRoutes extends RouteBuilder {

    private final MBkgLogger logger = MBkgLoggerFactory.getLogger(AuthenticationRoutes.class);
    private final StopWatch stopWatch;
    private final AuthenticationResponseHandler authenticationResponseHandler;

    public static final String START_AUTH_RESPONSE_HANDLER = "start('AuthenticationResponseHandler${routeId}',${exchange})";
    public static final String STOP_AUTH_RESPONSE_HANDLER = "stop('AuthenticationResponseHandler${routeId}',${exchange})";
    public static final String TIME_TAKEN_RESPONSE_HANDLER = "info('Authentication Response Handler completed(total time taken = ${bean:stopWatch?method=elapsed('AuthenticationResponseHandler${routeId}',${exchange})} ms)')";

    @Override
    public void configure() throws Exception {
        authenticateUser();
        registerUser();
    }

    private void authenticateUser() {
        from("direct:authenticateUser")
                .routeId("authid")
                .bean(stopWatch, START_AUTH_RESPONSE_HANDLER)
                .bean(authenticationResponseHandler, "authenticateUser")
                .bean(stopWatch, STOP_AUTH_RESPONSE_HANDLER)
                .bean(logger, TIME_TAKEN_RESPONSE_HANDLER);
    }

    private void registerUser() {
        from("direct:registerUser")
                .routeId("registerid")
                .bean(stopWatch, START_AUTH_RESPONSE_HANDLER)
                .bean(authenticationResponseHandler, "registerUser")
                .bean(stopWatch, STOP_AUTH_RESPONSE_HANDLER)
                .bean(logger, TIME_TAKEN_RESPONSE_HANDLER);
    }
}
