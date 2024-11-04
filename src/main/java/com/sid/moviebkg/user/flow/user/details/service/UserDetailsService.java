package com.sid.moviebkg.user.flow.user.details.service;

import com.sid.moviebkg.common.dto.user.UserDetailsDto;

public interface UserDetailsService {
    UserDetailsDto findUserByUserId(String userId);
}
