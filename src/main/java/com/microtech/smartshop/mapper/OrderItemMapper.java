package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.response.OrderItemResponse;
import com.microtech.smartshop.entity.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.nom", target = "productName")
    OrderItemResponse toResponse(OrderItem orderItem);
}
