package com.microtech.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "prix_ht", precision = 19, scale = 4)
    private BigDecimal prixHt;

    private Integer stock;

    @Column(nullable = false)
    private boolean deleted = false;
}
