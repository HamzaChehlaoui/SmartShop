package com.microtech.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
