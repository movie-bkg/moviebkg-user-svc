package com.sid.moviebkg.user.authentication.service;

import com.sid.moviebkg.common.dto.MessageDto;
import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.user.authentication.config.ResponseMsgConfiguration;
import com.sid.moviebkg.user.authentication.dto.AuthRequestDto;
import com.sid.moviebkg.user.authentication.dto.RegisterRequestDto;
import com.sid.moviebkg.user.authentication.dto.response.UserDto;
import com.sid.moviebkg.user.authentication.exception.AuthFailureException;
import com.sid.moviebkg.user.model.UserLogin;
import com.sid.moviebkg.user.authentication.repository.UserRepository;
import com.sid.moviebkg.user.authentication.utils.AuthUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sid.moviebkg.common.utils.ResponseMsgDtoUtil.findMsgAndPopulateResponse;
import static com.sid.moviebkg.common.utils.ResponseMsgDtoUtil.getRespMsg;
import static com.sid.moviebkg.user.util.UserCmnConstants.*;

@Service
public class AuthenticationResponseHandler {

    private MBkgLogger logger = MBkgLoggerFactory.getLogger(AuthenticationResponseHandler.class);
    private final UserRepository userRepository;
    private final ResponseMsgConfiguration msgConfiguration;
    private final ModelMapper modelMapper;
    private final AuthUtils authUtils;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    @Value("${token.disabled:#{null}}")
    private Boolean tokenDisabled;

    public AuthenticationResponseHandler(UserRepository userRepository, ResponseMsgConfiguration msgConfiguration,
                                         @Qualifier("userAuthModelMapper") ModelMapper modelMapper, AuthUtils authUtils,
                                         TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.msgConfiguration = msgConfiguration;
        this.modelMapper = modelMapper;
        this.authUtils = authUtils;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto authenticateUser(AuthRequestDto request) {
        UserLogin userLogin = UserLogin.builder().build();
        if (StringUtils.hasText(request.getLogin()) && StringUtils.hasText(request.getPassword())) {
            Optional<UserLogin> usr = userRepository.findByEmail(request.getLogin());
            if (usr.isEmpty() || !isPasswordValid(request.getPassword(), usr.get().getPassword())) {
                List<MessageDto> messageDtos = msgConfiguration.getMessages();
                ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(LOGIN_FAILED_WITH_ERROR, ERR_1004, messageDtos);
                throw new AuthFailureException(HttpStatus.BAD_REQUEST, responseMsgDto);
            }
            userLogin = usr.get();
        }
        UserDto userDto = modelMapper.map(userLogin, UserDto.class);
        if (userDto != null && (tokenDisabled != null && BooleanUtils.isFalse(tokenDisabled))) {
            String token = tokenService.generateToken(request.getLogin(), userLogin.getRole());
            userDto.setToken(token);
        }
        return userDto;
    }

    private boolean isPasswordValid(String incomingPswd, String dbPswd) {
        return passwordEncoder.matches(incomingPswd, dbPswd);
    }

    public UserDto registerUser(RegisterRequestDto requestDto) {
        UserDto userDto;
        try {
            UserLogin userLogin = modelMapper.map(requestDto, UserLogin.class);
            String encryptedPassword = passwordEncoder.encode(requestDto.getPassword());
            userLogin.setPassword(encryptedPassword);
            userLogin.setRole("USER");
            userLogin.setCreatedDateTime(LocalDateTime.now(Clock.systemUTC()));
            userLogin.setUpdatedDateTime(LocalDateTime.now(Clock.systemUTC()));
            UserLogin userLoginObj = userRepository.save(userLogin);
            userDto = modelMapper.map(userLoginObj, UserDto.class);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Exception occurred:{}", e.getMessage());
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            Map<String, Object[]> errorCodeArgs = new HashMap<>();
            if (e.getMessage().contains("ukusrnm")) {
                String username = requestDto.getUsername();
                authUtils.addToErrorCodeArgs(errorCodeArgs, ERR_1007, username, username);
            }
            if (e.getMessage().contains("ukusrem")) {
                String email = requestDto.getEmail();
                authUtils.addToErrorCodeArgs(errorCodeArgs, ERR_1008, email, email);
            }
            if (e.getMessage().contains("ukusrphn")) {
                String phone = requestDto.getPhone();
                authUtils.addToErrorCodeArgs(errorCodeArgs, ERR_1009, phone, phone);
            }
            ResponseMsgDto responseMsgDto = handleErrorMsg(errorCodeArgs, messageDtos);
            throw new AuthFailureException(HttpStatus.INTERNAL_SERVER_ERROR, responseMsgDto);
        } catch (Exception e) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(REGISTER_FAILED_WITH_ERROR, ERR_1006, messageDtos);
            throw new AuthFailureException(HttpStatus.INTERNAL_SERVER_ERROR, responseMsgDto);
        }
        return userDto;
    }

    private ResponseMsgDto handleErrorMsg(Map<String, Object[]> errorCodeArgs, List<MessageDto> messageDtos) {
        String expMsg = "Registration failed as Username/Email/Phone already exists";
        return getRespMsg(errorCodeArgs, expMsg, messageDtos);
    }
}
