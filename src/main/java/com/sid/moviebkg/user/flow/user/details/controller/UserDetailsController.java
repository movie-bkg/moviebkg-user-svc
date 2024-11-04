package com.sid.moviebkg.user.flow.user.details.controller;

import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.dto.user.UserDetailsDto;
import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.common.utils.ExceptionUtils;
import com.sid.moviebkg.user.flow.exception.UserFlowException;
import com.sid.moviebkg.user.flow.user.details.service.UserDetailsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sid.moviebkg.user.util.UserCmnConstants.CONTENT_TYPE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/details")
@SecurityRequirement(name = "Authorization")
public class UserDetailsController {

    private MBkgLogger logger = MBkgLoggerFactory.getLogger(UserDetailsController.class);
    private final ExceptionUtils exceptionUtils;
    private final UserDetailsService userDetailsService;

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDetailsDto fetchCardDetails(@PathVariable("userId") String userId) {
        UserDetailsDto response;
        try {
             response = userDetailsService.findUserByUserId(userId);
        } catch (Exception exception) {
            throw exceptionUtils.getException(exception, ex -> new UserFlowException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
                    UserFlowException.class::isInstance);
        }
        return response;
    }

    @ExceptionHandler(value = {UserFlowException.class})
    public ResponseEntity<Object> handleFailure(UserFlowException ex) {
        ResponseMsgDto responseMsgDto = ex.getResponseMsgDto();
        logger.warn("Failed with Error:{}", ex.getError());
        return ResponseEntity.status(ex.getStatus().value())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(responseMsgDto);
    }
}
