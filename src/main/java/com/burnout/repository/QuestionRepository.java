package com.burnout.repository;

import com.burnout.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    // Standard repository interface to fetch all evaluation questions at once from the database
}