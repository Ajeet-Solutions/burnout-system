package com.burnout;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.burnout.repository.QuestionRepository;
import com.burnout.model.Question; 

@SpringBootApplication
public class BurnoutSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BurnoutSystemApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(QuestionRepository repository) {
        return args -> {
            
            if (repository.count() == 0) {
                
                // Qus 1
                Question q1 = new Question();
                q1.setQuestionText("I feel tired at work.");
                repository.save(q1);

                // Qus 2
                Question q2 = new Question();
                q2.setQuestionText("I feel emotionally exhausted.");
                repository.save(q2);

                // Qus 3
                Question q3 = new Question();
                q3.setQuestionText("I feel stressed due to workload.");
                repository.save(q3);

                System.out.println("Your exact 3 assessment questions inserted successfully using setters!");
            }
        };
    }
}