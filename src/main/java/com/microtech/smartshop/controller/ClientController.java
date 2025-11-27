package com.microtech.smartshop.controller;

import com.microtech.smartshop.config.SecurityUtils;
import com.microtech.smartshop.dto.request.ClientUpdateRequest;
import com.microtech.smartshop.dto.response.ClientResponseDTO;
import com.microtech.smartshop.dto.response.CommandeHistoryResponse;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.service.ClientService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClient(
            @PathVariable Long id,
            HttpSession session) {
        SecurityUtils.checkClientAccess(session, id);
        return ResponseEntity.ok(clientService.getClient(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateRequest request,
            HttpSession session) {
        SecurityUtils.checkClientAccess(session, id);
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long id,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getAllClients(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(clientService.getAllClients(page, size, sortBy, direction));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<CommandeHistoryResponse>> getClientOrderHistory(
            @PathVariable Long id,
            HttpSession session) {
        SecurityUtils.checkClientAccess(session, id);
        return ResponseEntity.ok(clientService.getClientOrderHistory(id));
    }

    @GetMapping("/me")
    public ResponseEntity<ClientResponseDTO> getMyProfile(HttpSession session) {
        User currentUser = SecurityUtils.getCurrentUser(session);
        if (currentUser.getRole() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint réservé aux CLIENTs");
        }
        Long clientId = SecurityUtils.getCurrentClientId(session);
        return ResponseEntity.ok(clientService.getClient(clientId));
    }

    @GetMapping("/me/orders")
    public ResponseEntity<List<CommandeHistoryResponse>> getMyOrders(HttpSession session) {
        User currentUser = SecurityUtils.getCurrentUser(session);
        if (currentUser.getRole() != UserRole.CLIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Endpoint réservé aux CLIENTs");
        }
        Long clientId = SecurityUtils.getCurrentClientId(session);
        return ResponseEntity.ok(clientService.getClientOrderHistory(clientId));
    }
}
