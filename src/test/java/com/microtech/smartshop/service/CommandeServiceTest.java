package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.CommandeCreateRequest;
import com.microtech.smartshop.dto.request.OrderItemRequest;
import com.microtech.smartshop.dto.response.CommandeResponse;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.entity.Product;
import com.microtech.smartshop.enums.CustomerTier;
import com.microtech.smartshop.exception.BusinessRuleException;
import com.microtech.smartshop.mapper.CommandeMapper;
import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CommandeService
 */
@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CommandeMapper commandeMapper;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private CommandeServiceImpl commandeService;

    private Client testClient;
    private Product testProduct;
    private CommandeCreateRequest validCommandeRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commandeService, "tvaRate", new BigDecimal("0.20"));

        testClient = Client.builder()
                .id(1L)
                .name("Test Client")
                .tier(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .nom("Laptop HP")
                .stock(50)
                .prixHt(BigDecimal.valueOf(5000.00))
                .deleted(false)
                .build();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantite(2);

        validCommandeRequest = new CommandeCreateRequest();
        validCommandeRequest.setClientId(1L);
        validCommandeRequest.setItems(Collections.singletonList(itemRequest));
    }

    @Test
    void createCommande_ShouldSucceed_WithValidData() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArgument(0));
        when(commandeMapper.toResponse(any(Commande.class))).thenReturn(new CommandeResponse());

        CommandeResponse response = commandeService.createCommande(validCommandeRequest);

        assertNotNull(response);
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    void createCommande_ShouldFail_WhenInsufficientStock() {
        OrderItemRequest tooManyItems = new OrderItemRequest();
        tooManyItems.setProductId(1L);
        tooManyItems.setQuantite(100); // More than available stock (50)
        validCommandeRequest.setItems(Collections.singletonList(tooManyItems));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Exception exception = assertThrows(BusinessRuleException.class,
                () -> commandeService.createCommande(validCommandeRequest));

        assertTrue(exception.getMessage().contains("Stock insuffisant"));
    }
}
