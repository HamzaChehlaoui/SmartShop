package com.microtech.smartshop.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String nom;
    private BigDecimal prixHt;
    private Integer stock;
}