package com.burnout.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.burnout.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	// Legacy method: Returns a direct User object (May cause NullPointerException)
	// User findByEmail(String email);
	
	// Recommended approach: Returns a container object to safely handle null check operations
	Optional<User> findByEmail(String email);
}