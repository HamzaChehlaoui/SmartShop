package com.microtech.smartshop.dto.response;

import com.microtech.smartshop.enums.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeHistoryResponse {

    private Long id;
    private LocalDateTime dateCreation;
    private BigDecimal totalTTC;
    private OrderStatus status;
}
