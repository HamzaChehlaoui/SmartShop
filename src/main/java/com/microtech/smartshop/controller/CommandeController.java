package com.microtech.smartshop.controller;

import com.microtech.smartshop.config.SecurityUtils;
import com.microtech.smartshop.dto.request.CommandeCreateRequest;
import com.microtech.smartshop.dto.response.CommandeResponse;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.service.CommandeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    public ResponseEntity<CommandeResponse> createCommande(
            @Valid @RequestBody CommandeCreateRequest request,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        CommandeResponse response = commandeService.createCommande(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponse> getCommande(
            @PathVariable Long id,
            HttpSession session) {
        CommandeResponse response = commandeService.getCommande(id);

        SecurityUtils.checkClientAccess(session, response.getClientId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CommandeResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(commandeService.updateCommandeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelCommande(
            @PathVariable Long id,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        commandeService.cancelCommande(id);
        return ResponseEntity.noContent().build();
    }
}
