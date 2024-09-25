package com.sid.moviebkg.user.authentication.repository;

import com.sid.moviebkg.user.authentication.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserLogin, String> {
    Optional<UserLogin> findByEmailAndPassword(String email, String password);
    Optional<UserLogin> findByEmail(String email);
}