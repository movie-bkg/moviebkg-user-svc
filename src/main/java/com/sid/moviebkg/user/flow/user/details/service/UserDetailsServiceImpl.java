package com.sid.moviebkg.user.flow.user.details.service;

import com.sid.moviebkg.common.dto.MessageDto;
import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.dto.user.CardDetailsDto;
import com.sid.moviebkg.common.dto.user.PreferenceDto;
import com.sid.moviebkg.common.dto.user.UserDetailsDto;
import com.sid.moviebkg.common.model.user.CardDetails;
import com.sid.moviebkg.common.model.user.UserLogin;
import com.sid.moviebkg.common.model.user.UserPreference;
import com.sid.moviebkg.user.authentication.config.ResponseMsgConfiguration;
import com.sid.moviebkg.user.authentication.repository.UserRepository;
import com.sid.moviebkg.user.flow.exception.UserFlowException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.sid.moviebkg.common.utils.ResponseMsgDtoUtil.findMsgAndPopulateResponse;
import static com.sid.moviebkg.user.util.UserCmnConstants.ERR_1004;
import static com.sid.moviebkg.user.util.UserCmnConstants.OPERATION_FAILED_WITH_ERROR;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ResponseMsgConfiguration msgConfiguration;

    @Override
    public UserDetailsDto findUserByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                    .exception("Invalid request. Please provide the userId.")
                    .build();
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        Optional<UserLogin> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        UserLogin userObj = user.get();
        return UserDetailsDto.builder()
                .userId(userId)
                .username(userObj.getUsername())
                .firstName(userObj.getFirstName())
                .lastName(userObj.getLastName())
                .email(userObj.getEmail())
                .phone(userObj.getPhone())
                .preferences(getPreferences(userObj.getPreferences()))
                .cardDetails(getCardDetails(userObj.getCardDetails()))
                .build();
    }

    private List<CardDetailsDto> getCardDetails(List<CardDetails> cardDetails) {
        return cardDetails.stream()
                .map(card -> CardDetailsDto.builder()
                        .cardNo(card.getCardNo())
                        .cardType(card.getCardType())
                        .billingAddress(card.getBillingAddress())
                        .expiryDate(card.getExpiryDate().toString())
                        .build()).toList();
    }

    private List<PreferenceDto> getPreferences(List<UserPreference> preferences) {
        return preferences.stream()
                .map(userPreference -> PreferenceDto.builder()
                        .genre(userPreference.getGenre())
                        .language(userPreference.getLanguage())
                        .build()).toList();
    }
}
