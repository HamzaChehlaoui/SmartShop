package com.microtech.smartshop.repository;

import com.microtech.smartshop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCommandeId(Long commandeId);

    @Query("SELECT COALESCE(MAX(p.numeroPaiement), 0) FROM Payment p WHERE p.commande.id = :commandeId")
    Integer findMaxNumeroPaiementByCommandeId(Long commandeId);
}
