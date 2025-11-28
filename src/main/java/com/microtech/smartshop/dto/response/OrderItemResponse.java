package com.microtech.smartshop.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal totalLigne;
}
