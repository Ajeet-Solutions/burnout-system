package com.burnout.controller;

import com.burnout.model.CopingStrategy;
import com.burnout.repository.CopingStrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/strategies")
@CrossOrigin("*")
public class CopingStrategyController {

    @Autowired
    private CopingStrategyRepository strategyRepo;

    // Endpoint to provide dynamic recommendations based on the burnout level
    @GetMapping("/recommend")
    public ResponseEntity<List<CopingStrategy>> getRecommendations(@RequestParam String level) {
        List<CopingStrategy> strategies = strategyRepo.findByBurnoutLevel(level);
        return ResponseEntity.ok(strategies);
    }
}