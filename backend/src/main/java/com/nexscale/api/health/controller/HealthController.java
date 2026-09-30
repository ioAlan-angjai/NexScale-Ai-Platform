package com.nexscale.api.health.controller;

import com.nexscale.api.health.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Controller untuk memantau status kesehatan sistem backend Nexscale AI
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse(
            "UP",
            "nexscale-ai",
            Instant.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}
