package com.sid.moviebkg.user.flow.preference.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPreferenceDto {
    private String userId;
    private List<PreferenceDto> preferences;
}
