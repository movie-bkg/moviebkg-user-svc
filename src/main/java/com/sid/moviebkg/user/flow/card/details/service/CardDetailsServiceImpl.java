package com.sid.moviebkg.user.flow.card.details.service;

import com.sid.moviebkg.common.dto.MessageDto;
import com.sid.moviebkg.common.dto.ResponseMsgDto;
import com.sid.moviebkg.common.logging.MBkgLogger;
import com.sid.moviebkg.common.logging.MBkgLoggerFactory;
import com.sid.moviebkg.user.authentication.config.ResponseMsgConfiguration;
import com.sid.moviebkg.user.authentication.repository.UserRepository;
import com.sid.moviebkg.user.flow.card.details.dto.CardDetailsDto;
import com.sid.moviebkg.user.flow.card.details.dto.UserCardDetails;
import com.sid.moviebkg.user.flow.card.details.repository.CardDetailsRepository;
import com.sid.moviebkg.user.flow.exception.UserFlowException;
import com.sid.moviebkg.user.mapper.UserCmnMapper;
import com.sid.moviebkg.user.model.CardDetails;
import com.sid.moviebkg.user.model.UserLogin;
import com.sid.moviebkg.user.util.UserCmnUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.sid.moviebkg.common.utils.ResponseMsgDtoUtil.findMsgAndPopulateResponse;
import static com.sid.moviebkg.user.util.UserCmnConstants.ERR_1004;
import static com.sid.moviebkg.user.util.UserCmnConstants.OPERATION_FAILED_WITH_ERROR;

@Service
@RequiredArgsConstructor
public class CardDetailsServiceImpl implements CardDetailsService {

    private MBkgLogger logger = MBkgLoggerFactory.getLogger(CardDetailsServiceImpl.class);
    private final UserRepository userRepository;
    private final CardDetailsRepository cardDetailsRepository;
    private final UserCmnUtils userCmnUtils;
    private final UserCmnMapper userCmnMapper;
    private final ResponseMsgConfiguration msgConfiguration;

    @Override
    @Transactional
    public void saveCardDetails(UserCardDetails details) {
        if (!isRequestValid(details)) {
            ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                    .exception("Mandatory fields are not present. UserId and Card details are mandatory.")
                    .build();
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        Optional<UserLogin> user = userRepository.findById(details.getUserId());
        if (user.isEmpty()) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        List<CardDetailsDto> incomingCardDetails = details.getCardDetails();
        List<CardDetails> dbCardDetails = cardDetailsRepository.findByUser(user.get());
        List<CardDetailsDto> detailsToSave = userCmnUtils.filterList(incomingCardDetails, detailsDto -> !userCmnUtils.isAlreadyPresent(detailsDto, dbCardDetails,
                (cardDetailDto, dbCardDetail) -> dbCardDetail.getCardNo().equals(detailsDto.getCardNo())
                        || dbCardDetail.getExpiryDate().equals(LocalDate.parse(detailsDto.getExpiryDate()))));
        if (CollectionUtils.isEmpty(detailsToSave)) {
            ResponseMsgDto responseMsgDto = ResponseMsgDto.builder()
                    .exception("Card Details already exists.")
                    .build();
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        List<CardDetails> cardDetails = userCmnMapper.mapToCardDetails(detailsToSave, user.get());
        cardDetailsRepository.saveAll(cardDetails);
    }

    @Override
    public UserCardDetails findCardDetailsByUserId(String userId) {
        Optional<UserLogin> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            List<MessageDto> messageDtos = msgConfiguration.getMessages();
            ResponseMsgDto responseMsgDto = findMsgAndPopulateResponse(OPERATION_FAILED_WITH_ERROR, ERR_1004, messageDtos);
            throw new UserFlowException(HttpStatus.BAD_REQUEST, responseMsgDto);
        }
        List<CardDetails> cardDetailsObjs = cardDetailsRepository.findByUser(user.get());
        UserCardDetails userCardDetails = UserCardDetails.builder().build();
        if (!CollectionUtils.isEmpty(cardDetailsObjs)) {
            userCardDetails = userCmnMapper.mapToUserCardDetails(cardDetailsObjs);
            userCardDetails.setUserId(userId);
        }
        return userCardDetails;
    }

    private boolean isRequestValid(UserCardDetails details) {
        return details != null && details.getUserId() != null
                && StringUtils.hasText(details.getUserId())
                && !CollectionUtils.isEmpty(details.getCardDetails())
                && userCmnUtils.validateList(details.getCardDetails(),
                cardDetailsDto -> !StringUtils.hasText(cardDetailsDto.getCardType())
                        || !StringUtils.hasText(cardDetailsDto.getCardNo())
                        || !StringUtils.hasText(cardDetailsDto.getBillingAddress())
                        || !isDateValid(cardDetailsDto.getExpiryDate()));
    }

    private boolean isDateValid(String dateStr) {
        boolean isDateValid = false;
        try {
            LocalDate.parse(dateStr);
            isDateValid = true;
        } catch (Exception e) {
            logger.warn("Invalid Date:{}", dateStr);
        }
        return isDateValid;
    }
}
