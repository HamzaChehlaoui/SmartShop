package com.microtech.smartshop.entity;

import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Column(name = "numero_paiement", nullable = false)
    private Integer numeroPaiement;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_paiement", nullable = false)
    private PaymentType typePaiement;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "date_encaissement")
    private LocalDate dateEncaissement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;


    @Column(name = "reference_recu")
    private String referenceRecu;

    @Column(name = "numero_cheque")
    private String numeroCheque;

    @Column(name = "banque_cheque")
    private String banqueCheque;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "reference_virement")
    private String referenceVirement;

    @Column(name = "banque_virement")
    private String banqueVirement;
}
