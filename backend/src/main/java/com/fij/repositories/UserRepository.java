package com.fij.repositories;

import com.fij.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Cette simple ligne permet de générer la requête : SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}