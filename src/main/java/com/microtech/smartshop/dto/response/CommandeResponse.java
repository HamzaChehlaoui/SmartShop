package com.microtech.smartshop.dto.response;

import com.microtech.smartshop.enums.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeResponse {

    private Long id;
    private Long clientId;
    private String clientName;
    private LocalDateTime dateCreation;
    private BigDecimal sousTotalHt;
    private BigDecimal remise;
    private BigDecimal montantHTApresRemise;
    private BigDecimal tva;
    private BigDecimal totalTTC;
    private String promoCode;
    private OrderStatus status;
    private BigDecimal montantRestant;
    private List<OrderItemResponse> items;
    private List<PaymentResponse> payments;
}
