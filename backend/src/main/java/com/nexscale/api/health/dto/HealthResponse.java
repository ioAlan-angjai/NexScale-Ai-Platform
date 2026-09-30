package com.nexscale.api.health.dto;

/**
 * Data Transfer Object (DTO) untuk payload respons health check API
 */
public record HealthResponse(
    String status,
    String service,
    String timestamp
) {}
