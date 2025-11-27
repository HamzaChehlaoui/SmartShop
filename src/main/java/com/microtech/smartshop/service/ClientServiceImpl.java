package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.ClientUpdateRequest;
import com.microtech.smartshop.dto.response.ClientResponseDTO;
import com.microtech.smartshop.dto.response.CommandeHistoryResponse;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.enums.CustomerTier;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.ClientMapper;
import com.microtech.smartshop.mapper.CommandeMapper;
import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final ClientMapper clientMapper;
    private final CommandeMapper commandeMapper;

    @Override
    public ClientResponseDTO getClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID: " + id));
        return clientMapper.toResponseDTO(client);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID: " + id));

        if (request.getName() != null)
            client.setName(request.getName());
        if (request.getEmail() != null)
            client.setEmail(request.getEmail());
        if (request.getPhone() != null)
            client.setPhone(request.getPhone());
        if (request.getAddress() != null)
            client.setAddress(request.getAddress());

        return clientMapper.toResponseDTO(clientRepository.save(client));
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID: " + id));
        clientRepository.delete(client);
    }

    @Override
    public Page<ClientResponseDTO> getAllClients(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return clientRepository.findAll(pageable).map(clientMapper::toResponseDTO);
    }

    @Override
    public List<CommandeHistoryResponse> getClientOrderHistory(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client non trouvé avec ID: " + clientId);
        }

        List<Commande> commandes = commandeRepository.findByClientIdOrderByDateCreationDesc(clientId);
        return commandes.stream()
                .map(commandeMapper::toHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateClientLoyaltyTier(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec ID: " + clientId));

        Integer totalOrders = client.getTotalOrders() != null ? client.getTotalOrders() : 0;
        BigDecimal totalSpent = client.getTotalSpent() != null ? client.getTotalSpent() : BigDecimal.ZERO;

        CustomerTier newTier = calculateTier(totalOrders, totalSpent);

        if (client.getTier() != newTier) {
            client.setTier(newTier);
            clientRepository.save(client);
        }
    }

    private CustomerTier calculateTier(Integer totalOrders, BigDecimal totalSpent) {
        // PLATINUM: >= 20 commandes OU >= 15,000 DH
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        }
        // GOLD: >= 10 commandes OU >= 5,000 DH
        if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        }
        // SILVER: >= 3 commandes OU >= 1,000 DH
        if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }
        // BASIC: par défaut
        return CustomerTier.BASIC;
    }
}
