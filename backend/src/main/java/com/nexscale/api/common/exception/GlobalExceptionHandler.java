package com.nexscale.api.common.exception;

import com.nexscale.api.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;
import org.springframework.http.converter.HttpMessageNotReadableException;



import java.time.Instant;

/**
 * Global exception handler untuk menangkap semua exception dari controller
 * dan mengembalikan format JSON yang konsisten
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Menangani request ke endpoint yang tidak terdaftar (404 Not Found).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint tidak ditemukan: " + request.getRequestURI(),
                request.getRequestURI(),
                Instant.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Menangani data duplikat seperti email yang sudah terdaftar (409 Conflict).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Data Duplikat",
                ex.getMessage(),
                Instant.now().toString()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Catch-all handler untuk exception yang tidak tertangani secara spesifik (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Terjadi kesalahan internal pada server",
                request.getRequestURI(),
                Instant.now().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Menangani input yang melanggar aturan validasi (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ErrorResponse>handleValidationException(
        MethodArgumentNotValidException ex) {
               String message = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
               .collect(Collectors.joining("; "));

               ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Validasi Gagal", message, Instant.now().toString());
                return 
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Menangani request body yang tidak terbaca, seperti JSON rusak
         * atau nilai enum yang tidak dikenal (400 Bad Request)
         */

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse>handleMessageNotReadable(
                HttpMessageNotReadableException ex) {
                ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), 
                "Format Request Tidak Valid",
                "Format JSON atau tipe data pada request body tidak valid",
                
                Instant.now().toString() );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);


        }

        /**
         * Menangani pendaftaran role terlarang seperti admin secara publik (403 Forbidden)
         */

        @ExceptionHandler(RoleNotAllowedException.class)
        public ResponseEntity<ErrorResponse>handleRoleNotAllowed(RoleNotAllowedException ex) {
                ErrorResponse error = new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Akses Ditolak",
                        ex.getMessage(),

                Instant.now().toString());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
}