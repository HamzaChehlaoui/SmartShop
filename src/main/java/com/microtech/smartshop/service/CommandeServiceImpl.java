package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.CommandeCreateRequest;
import com.microtech.smartshop.dto.request.OrderItemRequest;
import com.microtech.smartshop.dto.response.CommandeResponse;
import com.microtech.smartshop.entity.*;
import com.microtech.smartshop.enums.CustomerTier;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.exception.BusinessRuleException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.CommandeMapper;
import com.microtech.smartshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final CommandeMapper commandeMapper;
    private final ClientService clientService;
    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUsageRepository promoCodeUsageRepository;

    @Value("${app.tva.rate:0.20}")
    private BigDecimal tvaRate;

    @Override
    public CommandeResponse createCommande(CommandeCreateRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Client non trouvé avec ID: " + request.getClientId()));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal sousTotalHt = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produit non trouvé avec ID: " + itemReq.getProductId()));

            if (product.isDeleted()) {
                throw new BusinessRuleException("Le produit " + product.getNom() + " n'est plus disponible");
            }

            if (product.getStock() < itemReq.getQuantite()) {
                throw new BusinessRuleException(
                        "Stock insuffisant pour le produit " + product.getNom() +
                                ". Disponible: " + product.getStock() + ", Demandé: " + itemReq.getQuantite());
            }

            BigDecimal totalLigne = product.getPrixHt()
                    .multiply(new BigDecimal(itemReq.getQuantite()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantite(itemReq.getQuantite())
                    .prixUnitaire(product.getPrixHt())
                    .totalLigne(totalLigne)
                    .build();

            orderItems.add(orderItem);
            sousTotalHt = sousTotalHt.add(totalLigne);
        }

        BigDecimal remise = calculateDiscount(client, sousTotalHt, request.getPromoCode());

        BigDecimal montantHTApresRemise = sousTotalHt.subtract(remise).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tva = montantHTApresRemise.multiply(tvaRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalTTC = montantHTApresRemise.add(tva).setScale(2, RoundingMode.HALF_UP);

        Commande commande = Commande.builder()
                .client(client)
                .sousTotalHt(sousTotalHt)
                .remise(remise)
                .montantHTApresRemise(montantHTApresRemise)
                .tva(tva)
                .totalTTC(totalTTC)
                .promoCode(request.getPromoCode())
                .status(OrderStatus.PENDING)
                .montantRestant(totalTTC)
                .build();

        for (OrderItem item : orderItems) {
            item.setCommande(commande);
        }
        commande.setOrderItems(orderItems);

        Commande savedCommande = commandeRepository.save(commande);

        if (request.getPromoCode() != null && !request.getPromoCode().isEmpty()) {
            promoCodeRepository.findByCodeAndIsActiveTrue(request.getPromoCode())
                    .ifPresent(promoCode -> {
                        PromoCodeUsage usage = PromoCodeUsage.builder()
                                .promoCode(promoCode)
                                .client(client)
                                .commande(savedCommande)
                                .build();
                        promoCodeUsageRepository.save(usage);

                        // Increment usage counter
                        promoCode.setCurrentUsages(promoCode.getCurrentUsages() + 1);
                        promoCodeRepository.save(promoCode);
                    });
        }

        return commandeMapper.toResponse(savedCommande);
    }

    @Override
    public CommandeResponse getCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec ID: " + id));
        return commandeMapper.toResponse(commande);
    }

    @Override
    public CommandeResponse updateCommandeStatus(Long id, OrderStatus newStatus) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec ID: " + id));

        if (commande.getStatus() == OrderStatus.CONFIRMED ||
                commande.getStatus() == OrderStatus.CANCELED ||
                commande.getStatus() == OrderStatus.REJECTED) {
            throw new BusinessRuleException(
                    "Impossible de modifier une commande avec statut final: " + commande.getStatus());
        }

        if (newStatus == OrderStatus.CONFIRMED) {
            if (commande.getMontantRestant().compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessRuleException("La commande doit être totalement payée avant validation. Restant: "
                        + commande.getMontantRestant() + " DH");
            }


            for (OrderItem item : commande.getOrderItems()) {
                Product product = item.getProduct();
                if(product.getStock() < item.getQuantite()){
                    throw new BusinessRuleException("Stock insuffisant pour le produit " + product.getNom() +
                            ". Disponible: " + product.getStock() + ", Demandé: " + item.getQuantite());
                }
                product.setStock(product.getStock() - item.getQuantite());
                productRepository.save(product);
            }

            Client client = commande.getClient();
            client.setTotalOrders((client.getTotalOrders() != null ? client.getTotalOrders() : 0) + 1);
            client.setTotalSpent((client.getTotalSpent() != null ? client.getTotalSpent() : BigDecimal.ZERO)
                    .add(commande.getTotalTTC()));

            if (client.getFirstOrderDate() == null) {
                client.setFirstOrderDate(LocalDateTime.now());
            }
            client.setLastOrderDate(LocalDateTime.now());

            clientRepository.save(client);

            clientService.updateClientLoyaltyTier(client.getId());
        }

        commande.setStatus(newStatus);
        Commande saved = commandeRepository.save(commande);
        return commandeMapper.toResponse(saved);
    }

    @Override
    public void cancelCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec ID: " + id));

        if (commande.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException("Seules les commandes en attente peuvent être annulées");
        }

        commande.setStatus(OrderStatus.CANCELED);
        commandeRepository.save(commande);
    }


    private BigDecimal calculateDiscount(Client client, BigDecimal sousTotalHt, String promoCode) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        switch (client.getTier()) {
            case SILVER:
                if (sousTotalHt.compareTo(new BigDecimal("500")) >= 0) {
                    discountAmount = sousTotalHt.multiply(new BigDecimal("0.05"));
                }
                break;
            case GOLD:
                if (sousTotalHt.compareTo(new BigDecimal("800")) >= 0) {
                    discountAmount = sousTotalHt.multiply(new BigDecimal("0.10"));
                }
                break;
            case PLATINUM:
                if (sousTotalHt.compareTo(new BigDecimal("1200")) >= 0) {
                    discountAmount = sousTotalHt.multiply(new BigDecimal("0.15"));
                }
                break;
            case BASIC:
            default:
                // No discount for BASIC
                break;
        }

        if (promoCode != null && !promoCode.isEmpty()) {
            if (!promoCode.matches("PROMO-[A-Z0-9]{4}")) {
                throw new BusinessRuleException("Format du code promo invalide. Format attendu: PROMO-XXXX");
            }

            PromoCode promoCodeEntity = promoCodeRepository.findByCodeAndIsActiveTrue(promoCode)
                    .orElseThrow(() -> new BusinessRuleException(
                            "Code promo '" + promoCode + "' invalide ou inactif"));

            LocalDateTime now = LocalDateTime.now();
            if (promoCodeEntity.getValidFrom() != null && now.isBefore(promoCodeEntity.getValidFrom())) {
                throw new BusinessRuleException(
                        "Le code promo '" + promoCode + "' n'est pas encore valide");
            }
            if (promoCodeEntity.getValidUntil() != null && now.isAfter(promoCodeEntity.getValidUntil())) {
                throw new BusinessRuleException(
                        "Le code promo '" + promoCode + "' a expiré");
            }

            if (promoCodeUsageRepository.existsByPromoCodeAndClient(promoCodeEntity, client)) {
                throw new BusinessRuleException(
                        "Vous avez déjà utilisé le code promo '" + promoCode + "'");
            }

            if (promoCodeEntity.getMaxUsages() != null) {
                long currentUsageCount = promoCodeUsageRepository.countByPromoCode(promoCodeEntity);
                if (currentUsageCount >= promoCodeEntity.getMaxUsages()) {
                    throw new BusinessRuleException(
                            "Le code promo '" + promoCode + "' a atteint sa limite d'utilisation");
                }
            }

            BigDecimal promoDiscount = sousTotalHt
                    .multiply(promoCodeEntity.getDiscountPercentage().divide(new BigDecimal("100")));
            discountAmount = discountAmount.add(promoDiscount);
        }

        return discountAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
