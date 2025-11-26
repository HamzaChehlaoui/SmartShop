package com.microtech.smartshop.dto.response;

import com.microtech.smartshop.enums.CustomerTier;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private CustomerTier tier;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
