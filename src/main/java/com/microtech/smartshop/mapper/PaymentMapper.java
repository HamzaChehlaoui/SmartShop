package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.response.PaymentResponse;
import com.microtech.smartshop.entity.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "commande.id", target = "commandeId")
    PaymentResponse toResponse(Payment payment);
}
