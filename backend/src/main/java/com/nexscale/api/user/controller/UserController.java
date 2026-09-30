package com.nexscale.api.user.controller;

import com.nexscale.api.user.dto.RegisterRequest;
import com.nexscale.api.user.dto.UserResponse;
import com.nexscale.api.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")

public class UserController {

    private final UserService userService;

    //constructor injection (menghubungkan controller ke service)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegisterRequest request){
       //1. Delegasikan proses bisnis ke user Service
       UserResponse response = userService.registerUser(request);
       //2. Kembalikan HTTP status 201 Created beserta data UserResponse di body 
       return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}