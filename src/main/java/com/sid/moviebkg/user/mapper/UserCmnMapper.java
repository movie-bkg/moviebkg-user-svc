package com.sid.moviebkg.user.mapper;

import com.sid.moviebkg.common.model.user.CardDetails;
import com.sid.moviebkg.common.model.user.UserLogin;
import com.sid.moviebkg.common.model.user.UserPreference;
import com.sid.moviebkg.user.flow.card.details.dto.CardDetailsDto;
import com.sid.moviebkg.user.flow.card.details.dto.UserCardDetails;
import com.sid.moviebkg.user.dto.PreferenceDto;
import com.sid.moviebkg.user.dto.UserPreferenceDto;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserCmnMapper {

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
                .preferences(userPreferences.stream()
                        .map(userPreference -> PreferenceDto.builder()
                                .genre(userPreference.getGenre())
                                .language(userPreference.getLanguage())
                                .build()).toList())
                .build();
    }

    public List<CardDetails> mapToCardDetails(List<CardDetailsDto> cardDetailsDtos, UserLogin user) {
        LocalDateTime localDateTime = LocalDateTime.now(Clock.systemUTC());
        return cardDetailsDtos.stream()
                .map(detailsDto -> CardDetails.builder()
                        .cardNo(detailsDto.getCardNo())
                        .cardType(detailsDto.getCardType())
                        .user(user)
                        .expiryDate(LocalDate.parse(detailsDto.getExpiryDate()))
                        .billingAddress(detailsDto.getBillingAddress())
                        .createdDateTime(localDateTime)
                        .updatedDateTime(localDateTime)
                        .build())
                .toList();
    }

    public UserCardDetails mapToUserCardDetails(List<CardDetails> cardDetails) {
        return UserCardDetails.builder()
                .cardDetails(cardDetails.stream()
                        .map(details -> CardDetailsDto.builder()
                                .cardNo(details.getCardNo())
                                .cardType(details.getCardType())
                                .billingAddress(details.getBillingAddress())
                                .expiryDate(String.valueOf(details.getExpiryDate()))
                                .build()).toList())
                .build();
    }
}
