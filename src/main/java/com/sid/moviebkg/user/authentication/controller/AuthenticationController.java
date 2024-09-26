package com.sid.moviebkg.user.authentication.controller;

import com.sid.moviebkg.common.dto.MessageDto;
import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.common.utils.ExceptionUtils;
import com.sid.moviebkg.user.authentication.config.ResponseMsgConfiguration;
import com.sid.moviebkg.user.authentication.dto.AuthRequestDto;
import com.sid.moviebkg.user.authentication.dto.RegisterRequestDto;
import com.sid.moviebkg.user.authentication.dto.response.RefreshTokenDto;
import com.sid.moviebkg.user.authentication.dto.response.UserDto;
import com.sid.moviebkg.user.authentication.exception.AuthFailureException;
import com.sid.moviebkg.user.authentication.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Predicate;

import static com.sid.moviebkg.common.utils.ResponseMsgDtoUtil.findMsgAndPopulateResponse;
import static com.sid.moviebkg.user.util.UserCmnConstants.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private MBkgLogger logger = MBkgLoggerFactory.getLogger(AuthenticationController.class);
    private final ProducerTemplate producerTemplate;
    private final ResponseMsgConfiguration msgConfiguration;
    private final TokenService tokenService;
    private final ExceptionUtils exceptionUtils;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto login(@RequestBody AuthRequestDto request) {
        if (isNotValid(request, requestDto -> ObjectUtils.anyNull(requestDto.getLogin(), requestDto.getPassword()) || !StringUtils.hasText(requestDto.getLogin()) ||
                !StringUtils.hasText(requestDto.getPassword()))) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(LOGIN_FAILED_WITH_ERROR, ERR_1003, messageDtos);
            throw new AuthFailureException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        UserDto userDto = producerTemplate.requestBody("direct:authenticateUser", request, UserDto.class);
        logger.info("UserDto response:{}", userDto);
        return userDto;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto register(@RequestBody RegisterRequestDto request) {
        if (!isRequestValid(request)) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(REGISTER_FAILED_WITH_ERROR, ERR_1005, messageDtos);
            throw new AuthFailureException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        UserDto userDto = producerTemplate.requestBody("direct:registerUser", request, UserDto.class);
        logger.info("UserDto response:{}", userDto);
        return userDto;
    }

    @GetMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> refresh(HttpServletRequest request) {
        ResponseEntity<Object> response;
        String token = null;
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder().build();
        try {
            final String authHeader = request.getHeader(AUTHORIZATION);
            if (StringUtils.hasText(authHeader)) {
                token = tokenService.generateRefreshToken(authHeader);
            }
            if (StringUtils.hasText(token)) {
                refreshTokenDto.setToken(token);
            }
            response = StringUtils.hasText(token) ? ResponseEntity.ok(refreshTokenDto) : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"exception\":\"Unable to refresh token\"}");
        } catch (Exception exception) {
            throw exceptionUtils.getException(exception, ex -> new AuthFailureException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
                    AuthFailureException.class::isInstance);
        }
        return response;
    }

    private boolean isRequestValid(RegisterRequestDto request) {
        return !ObjectUtils.anyNull(request.getUsername(), request.getPassword(), request.getFirstName(),
                request.getLastName(), request.getEmail(), request.getPhone()) && StringUtils.hasText(request.getUsername()) &&
                StringUtils.hasText(request.getPassword()) && StringUtils.hasText(request.getFirstName()) &&
                StringUtils.hasText(request.getLastName()) && StringUtils.hasText(request.getEmail()) &&
                StringUtils.hasText(request.getPhone());
    }

    private <T> boolean isNotValid(T request, Predicate<T> predicate) {
        return predicate.test(request);
    }

    @ExceptionHandler(value = {AuthFailureException.class})
    public ResponseEntity<Object> handleAuthFailure(AuthFailureException ex) {
        ResponseMsgDto responseMsgDto = ex.getResponseMsgDto();
        logger.warn("Failed with Error:{}", ex.getError());
        return ResponseEntity.status(ex.getStatus().value())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(responseMsgDto);
    }
}
