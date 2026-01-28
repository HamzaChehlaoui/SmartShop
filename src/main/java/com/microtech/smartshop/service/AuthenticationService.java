package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.LoginRequestDTO;
import com.microtech.smartshop.dto.request.UserCreateRequestDTO;
import com.microtech.smartshop.dto.response.UserResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface AuthenticationService {
    UserResponseDTO login(LoginRequestDTO request, HttpSession session);

    UserResponseDTO register(UserCreateRequestDTO request, HttpSession session);

    void logout(HttpSession session);
}
