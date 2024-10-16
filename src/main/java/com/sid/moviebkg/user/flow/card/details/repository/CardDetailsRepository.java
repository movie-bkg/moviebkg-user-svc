package com.sid.moviebkg.user.flow.card.details.repository;


import com.sid.moviebkg.common.model.user.CardDetails;
import com.sid.moviebkg.common.model.user.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardDetailsRepository extends JpaRepository<CardDetails, String> {
    List<CardDetails> findByUser(UserLogin user);
}
