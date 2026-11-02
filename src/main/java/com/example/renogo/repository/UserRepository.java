package com.example.renogo.repository;

import com.example.renogo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByUserIdIgnoreCase(String userId);
    boolean existsByEmail(String email);
}