package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.ClientUpdateRequest;
import com.microtech.smartshop.dto.response.ClientResponseDTO;
import com.microtech.smartshop.dto.response.CommandeHistoryResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ClientService {

    ClientResponseDTO getClient(Long id);

    ClientResponseDTO updateClient(Long id, ClientUpdateRequest request);

    void deleteClient(Long id);

    Page<ClientResponseDTO> getAllClients(int page, int size, String sortBy, String direction);

    List<CommandeHistoryResponse> getClientOrderHistory(Long clientId);

    void updateClientLoyaltyTier(Long clientId);
}
