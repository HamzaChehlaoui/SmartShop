package com.microtech.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreateRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String phone;
    private String address;
}
