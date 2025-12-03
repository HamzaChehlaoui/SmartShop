package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.response.ClientResponseDTO;
import com.microtech.smartshop.entity.Client;
import com.microtech.smartshop.enums.CustomerTier;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.ClientMapper;
import com.microtech.smartshop.repository.ClientRepository;
import com.microtech.smartshop.repository.CommandeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientService
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private CommandeRepository commandeRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;
    private ClientResponseDTO testClientResponse;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id(1L)
                .name("Ahmed El Fassi")
                .email("ahmed@example.com")
                .phone("0600000000")
                .address("Casablanca")
                .tier(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        testClientResponse = new ClientResponseDTO();
        testClientResponse.setId(1L);
        testClientResponse.setName("Ahmed El Fassi");
        testClientResponse.setEmail("ahmed@example.com");
        testClientResponse.setTier(CustomerTier.BASIC);
    }

    @Test
    void getClient_ShouldReturnClient_WhenExists() {

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientMapper.toResponseDTO(testClient)).thenReturn(testClientResponse);

        ClientResponseDTO result = clientService.getClient(1L);

        assertNotNull(result);
        assertEquals("Ahmed El Fassi", result.getName());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void getClient_ShouldThrowException_WhenNotFound() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> clientService.getClient(999L));

        assertTrue(exception.getMessage().contains("999"));
    }
}
