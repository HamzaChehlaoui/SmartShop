package com.microtech.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantite;

    @Column(name = "prix_unitaire", precision = 19, scale = 4)
    private BigDecimal prixUnitaire;

    @Column(name = "total_ligne", precision = 19, scale = 4)
    private BigDecimal totalLigne;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;
}
