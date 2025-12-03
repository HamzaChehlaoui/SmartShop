package com.microtech.smartshop.controller;

import com.microtech.smartshop.config.SecurityUtils;
import com.microtech.smartshop.dto.request.PaymentCreateRequest;
import com.microtech.smartshop.dto.response.PaymentResponse;
import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> addPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        PaymentResponse response = paymentService.addPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long id,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<PaymentResponse>> getCommandePayments(
            @PathVariable Long commandeId,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(paymentService.getCommandePayments(commandeId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status,
            HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, status));
    }
}
