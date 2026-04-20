package com.dsaanalyser.backend.repository;

import com.dsaanalyser.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the `users` table.
 *
 * Primarily used by:
 *  - UserService         → register and login flows
 *  - CustomUserDetailsService → Spring Security credential loading
 *  - AnalysisService     → resolving the authenticated user before saving a submission
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}