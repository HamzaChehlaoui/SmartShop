package com.microtech.smartshop.dto.request;

import com.microtech.smartshop.enums.PaymentType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateRequest {

    @NotNull(message = "Commande ID est obligatoire")
    private Long commandeId;

    @NotNull(message = "Montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Montant doit être > 0")
    private BigDecimal montant;

    @NotNull(message = "Type de paiement est obligatoire")
    private PaymentType typePaiement;

    @NotNull(message = "Date de paiement est obligatoire")
    private LocalDate datePaiement;

    // Pour ESPECES
    private String referenceRecu;

    // Pour CHEQUE
    private String numeroCheque;
    private String banqueCheque;
    private LocalDate dateEcheance;

    // Pour VIREMENT
    private String referenceVirement;
    private String banqueVirement;
}
