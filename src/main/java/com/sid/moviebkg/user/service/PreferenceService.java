package com.sid.moviebkg.user.service;

import com.sid.moviebkg.user.dto.UserPreferenceDto;

public interface PreferenceService {
    void savePreferences(UserPreferenceDto preferenceRequest);
    UserPreferenceDto findPreferencesByUserId(String userId);
}
