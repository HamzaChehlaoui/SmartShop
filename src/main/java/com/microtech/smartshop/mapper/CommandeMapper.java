package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.response.CommandeHistoryResponse;
import com.microtech.smartshop.dto.response.CommandeResponse;
import com.microtech.smartshop.entity.Commande;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class, PaymentMapper.class })
public interface CommandeMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "orderItems", target = "items")
    CommandeResponse toResponse(Commande commande);

    CommandeHistoryResponse toHistoryResponse(Commande commande);
}
