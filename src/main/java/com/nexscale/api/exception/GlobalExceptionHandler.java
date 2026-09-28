package com.nexscale.api.exception;

import com.nexscale.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

/**
 * Global exception handler untuk menangkap semua exception dari controller
 * dan mengembalikan format JSON yang konsisten
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Menangani request ke endpoint yang terdaftar (404 Not Found).
     * Spring boot 3.x melempar NoResourceFoundException untuk kasus ini.
     */


@ExceptionHandler(NoResourceFoundException.class)
    public 
    ResponseEntity<ErrorResponse>handleNotFound(
    NoResourceFoundException ex, HttpServletRequest request)
    {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Endpoint tidak ditemukan: " +
            request.getRequestURI(),
            request.getRequestURI(),
            Instant.now().toString()

        );

        return
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    /**
     * Catch-all handler untuk exception yang tidak tertangani secara spesifik.
     * Mengembalikan 500 Internal Server Error dengan pesan aman
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>handlerGenericException(
        Exception ex, HttpServletRequest request) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Terjadi kesalahan internal pada server",
                request.getRequestURI(),
                Instant.now().toString()
            );

            return
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    
}   
