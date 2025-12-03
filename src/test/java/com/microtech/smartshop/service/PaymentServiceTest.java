package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.PaymentCreateRequest;
import com.microtech.smartshop.dto.response.PaymentResponse;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.entity.Payment;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.enums.PaymentType;
import com.microtech.smartshop.exception.BusinessRuleException;
import com.microtech.smartshop.mapper.PaymentMapper;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Commande testCommande;
    private PaymentCreateRequest validPaymentRequest;

    @BeforeEach
    void setUp() {
        testCommande = Commande.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalTTC(BigDecimal.valueOf(30000.0))
                .montantRestant(BigDecimal.valueOf(30000.0))
                .build();

        validPaymentRequest = new PaymentCreateRequest();
        validPaymentRequest.setCommandeId(1L);
        validPaymentRequest.setMontant(BigDecimal.valueOf(500.0));
        validPaymentRequest.setTypePaiement(PaymentType.ESPECES);
        validPaymentRequest.setReferenceRecu("REC-2024-001");
        validPaymentRequest.setDatePaiement(LocalDate.now());
    }

    @Test
    void addPayment_ShouldSucceed_WithValidCashPayment() {
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(testCommande));
        when(paymentRepository.findMaxNumeroPaiementByCommandeId(1L)).thenReturn(0);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(new PaymentResponse());

        PaymentResponse response = paymentService.addPayment(validPaymentRequest);

        assertNotNull(response);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void addPayment_ShouldFail_WhenExceedsCashLimit() {
        validPaymentRequest.setMontant(BigDecimal.valueOf(25000.0));
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(testCommande));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, 
            () -> paymentService.addPayment(validPaymentRequest));
        
        assertTrue(exception.getMessage().contains("20"),
            "Should mention cash limit: " + exception.getMessage());
    }
}
