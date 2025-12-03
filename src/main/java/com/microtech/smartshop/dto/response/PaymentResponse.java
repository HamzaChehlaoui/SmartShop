package com.microtech.smartshop.dto.response;

import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.enums.PaymentType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long commandeId;
    private Integer numeroPaiement;
    private BigDecimal montant;
    private PaymentType typePaiement;
    private LocalDate datePaiement;
    private LocalDate dateEncaissement;
    private PaymentStatus status;

    // Champs optionnels selon le type
    private String referenceRecu;
    private String numeroCheque;
    private String banqueCheque;
    private LocalDate dateEcheance;
    private String referenceVirement;
    private String banqueVirement;
}
