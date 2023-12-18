package com.example.springsecurityoauth2.oauth2.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String loginId);
    Optional<User> findByLoginId(String loginId);
}