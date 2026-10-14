package com.nexscale.api.auth.controller;

import com.nexscale.api.auth.dto.LoginRequest;
import com.nexscale.api.auth.dto.LoginResponse;
import com.nexscale.api.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    //constructor injection(Menghubungkan controller ke service)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    //1. Delegasikan proses autentikasi ke Authservice
    LoginResponse response = authService.login(request);
    //2. kembalikan HTTP 200 OK beserta token dan metadata
    return ResponseEntity.ok(response);
    }
}