package com.microtech.smartshop.controller;

import com.microtech.smartshop.dto.request.UserCreateRequestDTO;
import com.microtech.smartshop.dto.request.LoginRequestDTO;
import com.microtech.smartshop.dto.response.UserResponseDTO;
import com.microtech.smartshop.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserCreateRequestDTO request,
            HttpSession session) {

        UserResponseDTO response = authenticationService.register(request, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
            HttpSession session) {
        UserResponseDTO response = authenticationService.login(request, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        authenticationService.logout(session);
        return ResponseEntity.ok("Logged out successfully");
    }
}
