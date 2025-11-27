package com.microtech.smartshop.entity;

import com.microtech.smartshop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private LocalDateTime dateCreation;

    @Column(precision = 19, scale = 4)
    private BigDecimal sousTotalHt;

    @Column(precision = 19, scale = 4)
    private BigDecimal remise;

    @Column(precision = 19, scale = 4)
    private BigDecimal montantHTApresRemise;

    @Column(precision = 19, scale = 4)
    private BigDecimal tve;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalTTC;

    private String promoCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(precision = 19, scale = 4)
    private BigDecimal montantRestant;

    // العلاقة مع OrderItem (OneToMany)
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
