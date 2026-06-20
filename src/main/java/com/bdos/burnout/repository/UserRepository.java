package com.bdos.burnout.repository;

import com.bdos.burnout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Yeh purana method duplicate check karne ke liye (Day 2)
    boolean existsByEmail(String email);

    // 2. Naya Method: Email ke zariye database se poora User object dhoondhne ke liye (Day 3 - Login)
    Optional<User> findByEmail(String email);
}