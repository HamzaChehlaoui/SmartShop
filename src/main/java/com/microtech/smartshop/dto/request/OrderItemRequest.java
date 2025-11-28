package com.microtech.smartshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    @NotNull(message = "Product ID est obligatoire")
    private Long productId;

    @NotNull(message = "Quantité est obligatoire")
    @Min(value = 1, message = "Quantité doit être >= 1")
    private Integer quantite;
}
