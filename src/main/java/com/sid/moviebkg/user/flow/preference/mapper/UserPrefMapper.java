package com.sid.moviebkg.user.flow.preference.mapper;

import com.sid.moviebkg.user.flow.preference.dto.PreferenceDto;
import com.sid.moviebkg.user.flow.preference.dto.UserPreferenceDto;
import com.sid.moviebkg.user.model.UserLogin;
import com.sid.moviebkg.user.model.UserPreference;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserPrefMapper {

    public List<UserPreference> mapToUserPreferences(List<PreferenceDto> preferenceDtos, UserLogin user) {
        LocalDateTime localDateTime = LocalDateTime.now(Clock.systemUTC());
        return preferenceDtos.stream()
                .map(preferenceDto -> UserPreference.builder()
                        .genre(preferenceDto.getGenre())
                        .language(preferenceDto.getLanguage())
                        .user(user)
                        .createdDateTime(localDateTime)
                        .updatedDateTime(localDateTime)
                        .build())
                .toList();
    }

    public UserPreferenceDto mapToUserPreferenceDto(List<UserPreference> userPreferences) {
        return UserPreferenceDto.builder()
                .preferences(userPreferences.stream().map(userPreference -> PreferenceDto.builder()
                        .genre(userPreference.getGenre())
                        .language(userPreference.getLanguage())
                        .build()).toList())
                .build();
    }

}
