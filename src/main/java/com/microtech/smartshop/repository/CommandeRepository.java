package com.microtech.smartshop.repository;

import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByClientIdOrderByDateCreationDesc(Long clientId);

    List<Commande> findByClientIdAndStatus(Long clientId, OrderStatus status);

    long countByClientIdAndStatus(Long clientId, OrderStatus status);
}
