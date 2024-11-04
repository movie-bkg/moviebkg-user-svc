package com.sid.moviebkg.user.service;

import com.sid.moviebkg.common.dto.MessageDto;
import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.model.user.UserLogin;
import com.sid.moviebkg.common.model.user.UserPreference;
import com.sid.moviebkg.common.utils.ValidationUtil;
import com.sid.moviebkg.user.authentication.config.ResponseMsgConfiguration;
import com.sid.moviebkg.user.authentication.repository.UserRepository;
import com.sid.moviebkg.user.flow.exception.UserFlowException;
import com.sid.moviebkg.user.dto.PreferenceDto;
import com.sid.moviebkg.user.dto.UserPreferenceDto;
import com.sid.moviebkg.user.repository.UserPreferenceRepository;
import com.sid.moviebkg.user.mapper.UserCmnMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sid.moviebkg.common.utils.ResponseMsgDtoUtil.findMsgAndPopulateResponse;
import static com.sid.moviebkg.user.util.UserCmnConstants.ERR_1004;
import static com.sid.moviebkg.user.util.UserCmnConstants.OPERATION_FAILED_WITH_ERROR;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final UserCmnMapper userCmnMapper;
    private final ResponseMsgConfiguration msgConfiguration;

    @Override
    @Transactional
    public void savePreferences(UserPreferenceDto preferenceRequest) {
        if (isRequestValid(preferenceRequest)) {
            Optional<UserLogin> user = userRepository.findById(preferenceRequest.getUserId());
            if (user.isEmpty()) {
                List<MessageDto> messageDtos = msgConfiguration.getMessages();
                ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
                throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
            }
            List<PreferenceDto> incomingPreferences = preferenceRequest.getPreferences();
            List<UserPreference> dbPreferences = preferenceRepository.findByUser(user.get());
            List<PreferenceDto> preferencesToSave = validationUtil.filterList(incomingPreferences, incoming -> !validationUtil.isAlreadyPresent(
                    incoming, dbPreferences, (preferenceDto, dbPreference) -> Objects.equals(dbPreference.getGenre(), incoming.getGenre())
                            && Objects.equals(dbPreference.getLanguage(), incoming.getLanguage())
            ));
            if (CollectionUtils.isEmpty(preferencesToSave)) {
                ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                        .exception("Preferences already exists.")
                        .build();
                throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
            }
            List<UserPreference> userPreferences = userCmnMapper.mapToUserPreferences(preferencesToSave, user.get());
            preferenceRepository.saveAll(userPreferences);
        } else {
            ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                    .exception("Mandatory fields are not present. UserId and Preferences are mandatory.")
                    .build();
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
    }

    @Override
    public UserPreferenceDto findPreferencesByUserId(String userId) {
        Optional<UserLogin> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        List<UserPreference> userPrefObjs = preferenceRepository.findByUser(user.get());
        UserPreferenceDto userPreferenceDto = UserPreferenceDto.builder().build();
        if (!CollectionUtils.isEmpty(userPrefObjs)) {
            userPreferenceDto = userCmnMapper.mapToUserPreferenceDto(userPrefObjs);
            userPreferenceDto.setUserId(userId);
        }
        return userPreferenceDto;
    }

    private boolean isRequestValid(UserPreferenceDto preferenceRequest) {
        return preferenceRequest != null && preferenceRequest.getUserId() != null
                && StringUtils.hasText(preferenceRequest.getUserId())
                && !CollectionUtils.isEmpty(preferenceRequest.getPreferences())
                && validationUtil.validateList(preferenceRequest.getPreferences(), preferenceDto ->
                !StringUtils.hasText(preferenceDto.getGenre())
                || !StringUtils.hasText(preferenceDto.getLanguage()));
    }
}
