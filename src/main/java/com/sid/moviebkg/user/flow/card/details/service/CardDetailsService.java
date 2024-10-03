package com.sid.moviebkg.user.flow.card.details.service;

import com.sid.moviebkg.user.flow.card.details.dto.UserCardDetails;

public interface CardDetailsService {
    void saveCardDetails(UserCardDetails details);
    UserCardDetails findCardDetailsByUserId(String userId);
}
