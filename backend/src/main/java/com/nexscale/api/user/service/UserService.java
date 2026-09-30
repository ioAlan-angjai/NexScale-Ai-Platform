package com.nexscale.api.user.service;

import com.nexscale.api.common.exception.DuplicateResourceException;
import com.nexscale.api.user.dto.RegisterRequest;
import com.nexscale.api.user.dto.UserResponse;
import com.nexscale.api.user.entity.User;
import com.nexscale.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    //1. Deklarasi field repository
    private final UserRepository userRepository;

    //2. Constructor Injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        //1. validasi aturan bisnis: Email yang sudah terdaftar tidak boleh mendaftar lagi
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email sudah terdaftar: " + request.getEmail());
        }
        //2.Transformasi DTO Request ke Entity Database(Sementara password disimpan mentah sebelum tahap integrasi enkripsi menggunakan BCrypt)
        User user = new User(request.getEmail(), request.getPassword(), request.getRole(), request.getFullName());
        //3. simpan entity ke database via UserRepository
        User savedUser = userRepository.save(user);
        //4.Return respon dalam bentuk DTO aman(tanpa password)
        return UserResponse.fromEntity(savedUser);
    }
    
}
