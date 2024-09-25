package com.sid.moviebkg.user.authentication.utils;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class AuthUtils {

    public void addToErrorCodeArgs(Map<String, Object[]> errorCodeArgs, String errorCode, String mandatoryArg, Object... arguments) {
        if (StringUtils.hasText(mandatoryArg)) {
            errorCodeArgs.put(errorCode, arguments);
        }
    }
}
