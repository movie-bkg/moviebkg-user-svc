package com.sid.moviebkg.user.flow.preference.service;

import com.sid.moviebkg.common.dto.MessageDto;
import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.user.authentication.config.ResponseMsgConfiguration;
import com.sid.moviebkg.user.authentication.repository.UserRepository;
import com.sid.moviebkg.user.flow.preference.dto.PreferenceDto;
import com.sid.moviebkg.user.flow.preference.dto.UserPreferenceDto;
import com.sid.moviebkg.user.flow.preference.exception.UserNotFoundException;
import com.sid.moviebkg.user.flow.preference.mapper.UserPrefMapper;
import com.sid.moviebkg.user.flow.preference.repository.UserPreferenceRepository;
import com.sid.moviebkg.user.model.UserLogin;
import com.sid.moviebkg.user.model.UserPreference;
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
import static com.sid.moviebkg.user.util.UserCmnConstants.*;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

    private final UserPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final UserPrefMapper userPrefMapper;
    private final ResponseMsgConfiguration msgConfiguration;

    @Override
    @Transactional
    public void savePreferences(UserPreferenceDto preferenceRequest) {
        if (isRequestValid(preferenceRequest)) {
            Optional<UserLogin> user = userRepository.findById(preferenceRequest.getUserId());
            if (user.isEmpty()) {
                List<MessageDto> messageDtos = msgConfiguration.getMessages();
                ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
                throw new UserNotFoundException(HttpStatus.BAD_REQUEST, responseMsgDto);
            }
            List<PreferenceDto> incomingPreferences = preferenceRequest.getPreferences();
            List<UserPreference> dbPreferences = preferenceRepository.findByUser(user.get());
            List<PreferenceDto> preferencesToSave = filterPreferences(incomingPreferences, dbPreferences);
            if (CollectionUtils.isEmpty(preferencesToSave)) {
                ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                        .exception("Preferences already exists.")
                        .build();
                throw new UserNotFoundException(HttpStatus.BAD_REQUEST, responseMsgDto);
            }
            List<UserPreference> userPreferences = userPrefMapper.mapToUserPreferences(preferencesToSave, user.get());
            preferenceRepository.saveAll(userPreferences);
        } else {
            ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                    .exception("Mandatory fields are not present. UserId and Preferences are mandatory.")
                    .build();
            throw new UserNotFoundException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
    }

    private List<PreferenceDto> filterPreferences(List<PreferenceDto> incomingPreferences, List<UserPreference> dbPreferences) {
        return incomingPreferences.stream()
                .filter(preferenceDto -> !isAlreadyPresent(preferenceDto, dbPreferences))
                .toList();
    }

    private boolean isAlreadyPresent(PreferenceDto preferenceDto, List<UserPreference> dbPreferences) {
        boolean isPresent = false;
        for (UserPreference dbPreference : dbPreferences) {
            if (Objects.equals(dbPreference.getGenre(), preferenceDto.getGenre()) && Objects.equals(dbPreference.getLanguage(), preferenceDto.getLanguage())) {
                isPresent = true;
                break;
            }
        }
        return isPresent;
    }

    @Override
    public UserPreferenceDto findPreferencesByUserId(String userId) {
        Optional<UserLogin> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
            throw new UserNotFoundException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        List<UserPreference> userPrefObjs = preferenceRepository.findByUser(user.get());
        UserPreferenceDto userPreferenceDto = UserPreferenceDto.builder().build();
        if (!CollectionUtils.isEmpty(userPrefObjs)) {
            userPreferenceDto = userPrefMapper.mapToUserPreferenceDto(userPrefObjs);
            userPreferenceDto.setUserId(userId);
        }
        return userPreferenceDto;
    }

    private boolean isRequestValid(UserPreferenceDto preferenceRequest) {
        return preferenceRequest != null && preferenceRequest.getUserId() != null
                && StringUtils.hasText(preferenceRequest.getUserId())
                && !CollectionUtils.isEmpty(preferenceRequest.getPreferences());
    }
}
