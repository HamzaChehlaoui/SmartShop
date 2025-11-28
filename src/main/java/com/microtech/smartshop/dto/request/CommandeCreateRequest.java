package com.microtech.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeCreateRequest {

    @NotNull(message = "Client ID est obligatoire")
    private Long clientId;

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    private List<OrderItemRequest> items;

    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Format du code promo invalide (PROMO-XXXX)")
    private String promoCode;
}
