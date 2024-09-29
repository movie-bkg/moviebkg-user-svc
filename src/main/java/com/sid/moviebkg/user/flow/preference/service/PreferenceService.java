package com.sid.moviebkg.user.flow.preference.service;

import com.sid.moviebkg.user.flow.preference.dto.UserPreferenceDto;

public interface PreferenceService {
    void savePreferences(UserPreferenceDto preferenceRequest);
    UserPreferenceDto findPreferencesByUserId(String userId);
}
