package com.nexscale.api.common.dto;

/**
 * DTO untuk standarisasi format response error di seluruh API Website ini
 * Semua error (4xx, 5xx) akan menggunakan format ini
 */
public record ErrorResponse(
    int status,
    String error,
    String message,
    String timestamp
) {}
