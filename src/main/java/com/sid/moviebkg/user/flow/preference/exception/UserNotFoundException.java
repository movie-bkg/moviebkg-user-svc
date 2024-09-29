package com.sid.moviebkg.user.flow.preference.exception;

import com.sid.moviebkg.common.dto.ResponseMsgDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 30L;

    private final HttpStatus status;
    private final transient Object error;
    private final ResponseMsgDto responseMsgDto;

    public UserNotFoundException() {
        super("Login Failure");
        error = "Login Failure";
        status = HttpStatus.BAD_REQUEST;
        this.responseMsgDto = null;
    }

    public UserNotFoundException(String message) {
        super(message);
        status = HttpStatus.BAD_REQUEST;
        this.error = message;
        this.responseMsgDto = null;
    }

    public UserNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.error = message;
        this.responseMsgDto = null;
    }

    public UserNotFoundException(HttpStatus status, Exception e, ResponseMsgDto responseMsgDto) {
        super(e);
        this.status = status;
        this.error = e;
        this.responseMsgDto = responseMsgDto;
    }

    public UserNotFoundException(HttpStatus status, Error e, ResponseMsgDto responseMsgDto) {
        super(responseMsgDto.getException());
        this.status = status;
        this.error = e;
        this.responseMsgDto = responseMsgDto;
    }

    public UserNotFoundException(HttpStatus status, ResponseMsgDto responseMsgDto) {
        super(responseMsgDto.getException());
        this.status = status;
        this.error = responseMsgDto;
        this.responseMsgDto = responseMsgDto;
    }
}
