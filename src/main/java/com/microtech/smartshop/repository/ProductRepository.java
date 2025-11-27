package com.microtech.smartshop.repository;

import com.microtech.smartshop.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDeletedFalse(Pageable pageable);
    Page<Product> findByDeletedFalseAndNomContainingIgnoreCase(String nom, Pageable pageable);
}
