package com.microtech.smartshop.entity;

import com.microtech.smartshop.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier;

    private Integer totalOrders;

    private BigDecimal totalSpent;

    private LocalDateTime firstOrderDate;

    private LocalDateTime lastOrderDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalOrders == null) this.totalOrders = 0;
        if (this.totalSpent == null) this.totalSpent = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
