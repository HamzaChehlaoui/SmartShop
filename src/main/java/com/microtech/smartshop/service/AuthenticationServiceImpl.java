package com.microtech.smartshop.service;

import com.microtech.smartshop.config.SessionInterceptor;
import com.microtech.smartshop.dto.request.ClientCreateRequestDTO;
import com.microtech.smartshop.dto.request.LoginRequestDTO;
import com.microtech.smartshop.dto.request.UserCreateRequestDTO;
import com.microtech.smartshop.dto.response.UserResponseDTO;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.CustomerTier;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.mapper.UserMapper;
import com.microtech.smartshop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthenticationServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO register(UserCreateRequestDTO request, HttpSession session) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username already exists");
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        User user = User.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .role(UserRole.CLIENT)
                .enabled(true)
                .build();

        ClientCreateRequestDTO clientDTO = request.getClient();

        Client client = Client.builder()
                .name(clientDTO.getName())
                .email(clientDTO.getEmail())
                .phone(clientDTO.getPhone())
                .address(clientDTO.getAddress())
                .tier(CustomerTier.BASIC)
                .totalOrders(0)
                .user(user)
                .build();

        user.setClient(client);

        userRepository.save(user);

        SessionInterceptor.setLoggedUser(session, user);

        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO login(LoginRequestDTO request, HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        SessionInterceptor.setLoggedUser(session, user);

        return userMapper.toResponseDTO(user);
    }

    @Override
    public void logout(HttpSession session) {
        SessionInterceptor.clearSession(session);
    }
}
