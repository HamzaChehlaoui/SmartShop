package com.microtech.smartshop.dto.request;

import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.enums.UserRole;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequestDTO {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 100)
    private String password;

    private ClientCreateRequestDTO  client ;

}
