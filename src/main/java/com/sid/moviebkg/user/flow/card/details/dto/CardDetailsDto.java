package com.sid.moviebkg.user.flow.card.details.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDetailsDto {
    private String cardNo;
    private String cardType;
    private String expiryDate;
    private String billingAddress;
}
