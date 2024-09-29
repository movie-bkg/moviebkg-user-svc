package com.sid.moviebkg.user.flow.preference.repository;

import com.sid.moviebkg.user.model.UserLogin;
import com.sid.moviebkg.user.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, String> {
    List<UserPreference> findByUser(UserLogin user);
}
