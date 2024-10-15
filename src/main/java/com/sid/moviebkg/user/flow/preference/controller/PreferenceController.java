package com.sid.moviebkg.user.flow.preference.controller;

import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.common.utils.ExceptionUtils;
import com.sid.moviebkg.user.flow.preference.dto.UserPreferenceDto;
import com.sid.moviebkg.user.flow.exception.UserFlowException;
import com.sid.moviebkg.user.flow.preference.service.PreferenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.sid.moviebkg.user.util.UserCmnConstants.CONTENT_TYPE;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@RequestMapping("/user/preferences")
public class PreferenceController {

    private MBkgLogger logger = MBkgLoggerFactory.getLogger(PreferenceController.class);
    private final PreferenceService preferenceService;
    private final ExceptionUtils exceptionUtils;

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> savePreferences(@RequestBody UserPreferenceDto requestDto) {
        try {
            preferenceService.savePreferences(requestDto);
        } catch (Exception exception) {
            throw exceptionUtils.getException(exception, ex -> new UserFlowException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()),
                    UserFlowException.class::isInstance);
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Saved successfully\"}");
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserPreferenceDto fetchPreferences(@PathVariable("userId") String userId) {
        UserPreferenceDto response;
        try {
            if (StringUtils.hasText(userId)) {
                response = preferenceService.findPreferencesByUserId(userId);
            } else {
                ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                        .exception("Mandatory fields are not present. UserId is mandatory.")
                        .build();
                throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
            }
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
