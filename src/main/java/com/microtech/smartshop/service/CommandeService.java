package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.CommandeCreateRequest;
import com.microtech.smartshop.dto.response.CommandeResponse;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Map;

public interface CommandeService {

    CommandeResponse createCommande(CommandeCreateRequest request);

    CommandeResponse getCommande(Long id);

    CommandeResponse updateCommandeStatus(Long id, OrderStatus newStatus);

    void cancelCommande(Long id);
}
