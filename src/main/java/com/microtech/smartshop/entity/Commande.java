package com.microtech.smartshop.entity;

import com.microtech.smartshop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private LocalDateTime dateCreation;

    @Column(precision = 19, scale = 4)
    private BigDecimal sousTotalHt; // Somme de (prix HT × quantité)

    @Column(precision = 19, scale = 4)
    private BigDecimal remise;

    @Column(precision = 19, scale = 4)
    private BigDecimal montantHTApresRemise; // Sous-total HT - Remise

    @Column(precision = 19, scale = 4)
    private BigDecimal tva;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalTTC; // Montant HT après remise + TVA

    private String promoCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(precision = 19, scale = 4)
    private BigDecimal montantRestant;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
}
