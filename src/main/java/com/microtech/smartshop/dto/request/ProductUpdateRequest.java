package com.microtech.smartshop.dto.request;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotBlank
    private String nom;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal prixHt;

    @NotNull
    @Min(0)
    private Integer stock;
}