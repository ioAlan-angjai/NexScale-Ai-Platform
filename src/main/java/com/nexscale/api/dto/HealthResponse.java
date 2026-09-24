package com.nexscale.api.dto;

/**
 * Data transfer Object(DTO) untuk payload respons health check api
 */

public record HealthResponse(
    String status,
    String service,
    String timestamp
) {}
