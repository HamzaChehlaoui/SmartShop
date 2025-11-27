package com.microtech.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUpdateRequest {

    @Size(min = 3, max = 100, message = "Nom doit avoir entre 3 et 100 caractères")
    private String name;

    @Email(message = "Email invalide")
    private String email;

    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", message = "Numéro de téléphone invalide")
    private String phone;

    private String address;
}
