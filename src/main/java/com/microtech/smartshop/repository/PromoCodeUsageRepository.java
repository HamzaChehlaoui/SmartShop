package com.microtech.smartshop.repository;

import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.PromoCode;
import com.microtech.smartshop.entity.PromoCodeUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeUsageRepository extends JpaRepository<PromoCodeUsage, Long> {

    boolean existsByPromoCodeAndClient(PromoCode promoCode, Client client);

    long countByPromoCode(PromoCode promoCode);
}
