package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.PaymentCreateRequest;
import com.microtech.smartshop.dto.response.PaymentResponse;
import com.microtech.smartshop.entity.Commande;
import com.microtech.smartshop.entity.Payment;
import com.microtech.smartshop.enums.OrderStatus;
import com.microtech.smartshop.enums.PaymentStatus;
import com.microtech.smartshop.enums.PaymentType;
import com.microtech.smartshop.exception.BusinessRuleException;
import com.microtech.smartshop.exception.ResourceNotFoundException;
import com.microtech.smartshop.mapper.PaymentMapper;
import com.microtech.smartshop.repository.CommandeRepository;
import com.microtech.smartshop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CommandeRepository commandeRepository;
    private final PaymentMapper paymentMapper;

    private static final BigDecimal ESPECES_LIMIT = new BigDecimal("20000");

    @Override
    public PaymentResponse addPayment(PaymentCreateRequest request) {
        Commande commande = commandeRepository.findById(request.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée avec ID: " + request.getCommandeId()));

        if (commande.getStatus() != OrderStatus.PENDING) {
            throw new BusinessRuleException(
                    "Impossible d'ajouter un paiement à une commande avec statut: " + commande.getStatus());
        }

        if (request.getMontant().compareTo(commande.getMontantRestant()) > 0) {
            throw new BusinessRuleException(
                    "Le montant du paiement (" + request.getMontant() +
                            " DH) dépasse le montant restant (" + commande.getMontantRestant() + " DH)");
        }

        validatePaymentType(request);

        Integer maxNumero = paymentRepository.findMaxNumeroPaiementByCommandeId(request.getCommandeId());
        Integer numeroPaiement = (maxNumero != null ? maxNumero : 0) + 1;

        PaymentStatus initialStatus;
        LocalDate dateEncaissement = null;

        if (request.getTypePaiement() == PaymentType.ESPECES) {
            initialStatus = PaymentStatus.ENCAISSE;
            dateEncaissement = request.getDatePaiement();
        } else {
            initialStatus = PaymentStatus.EN_ATTENTE;
        }

        Payment payment = Payment.builder()
                .commande(commande)
                .numeroPaiement(numeroPaiement)
                .montant(request.getMontant())
                .typePaiement(request.getTypePaiement())
                .datePaiement(request.getDatePaiement())
                .dateEncaissement(dateEncaissement)
                .status(initialStatus)
                .referenceRecu(request.getReferenceRecu())
                .numeroCheque(request.getNumeroCheque())
                .banqueCheque(request.getBanqueCheque())
                .dateEcheance(request.getDateEcheance())
                .referenceVirement(request.getReferenceVirement())
                .banqueVirement(request.getBanqueVirement())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        if (initialStatus == PaymentStatus.ENCAISSE) {
            BigDecimal newMontantRestant = commande.getMontantRestant().subtract(request.getMontant());
            commande.setMontantRestant(newMontantRestant);
            commandeRepository.save(commande);
        }

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec ID: " + paymentId));

        if (payment.getStatus() == PaymentStatus.REJETE) {
            throw new BusinessRuleException("Impossible de modifier un paiement rejeté");
        }

        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(newStatus);

        if (newStatus == PaymentStatus.ENCAISSE && payment.getDateEncaissement() == null) {
            payment.setDateEncaissement(LocalDate.now());
        }

        Payment saved = paymentRepository.save(payment);

        Commande commande = payment.getCommande();

        if (oldStatus != PaymentStatus.ENCAISSE && newStatus == PaymentStatus.ENCAISSE) {
            BigDecimal newRestant = commande.getMontantRestant().subtract(payment.getMontant());
            commande.setMontantRestant(newRestant);
            commandeRepository.save(commande);
        } else if (oldStatus == PaymentStatus.ENCAISSE && newStatus == PaymentStatus.REJETE) {
            BigDecimal newRestant = commande.getMontantRestant().add(payment.getMontant());
            commande.setMontantRestant(newRestant);
            commandeRepository.save(commande);
        }

        return paymentMapper.toResponse(saved);
    }

    @Override
    public List<PaymentResponse> getCommandePayments(Long commandeId) {
        if (!commandeRepository.existsById(commandeId)) {
            throw new ResourceNotFoundException("Commande non trouvée avec ID: " + commandeId);
        }

        List<Payment> payments = paymentRepository.findByCommandeId(commandeId);
        return payments.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec ID: " + id));
        return paymentMapper.toResponse(payment);
    }

    /**
     * Validate payment according to type
     */
    private void validatePaymentType(PaymentCreateRequest request) {
        switch (request.getTypePaiement()) {
            case ESPECES:
                if (request.getMontant().compareTo(ESPECES_LIMIT) > 0) {
                    throw new BusinessRuleException(
                            "Le paiement en espèces ne peut pas dépasser 20,000 DH (limite légale). " +
                                    "Montant demandé: " + request.getMontant() + " DH");
                }
                if (request.getReferenceRecu() == null || request.getReferenceRecu().isEmpty()) {
                    throw new BusinessRuleException("Un reçu est obligatoire pour un paiement en espèces");
                }
                break;

            case CHEQUE:
                if (request.getNumeroCheque() == null || request.getNumeroCheque().isEmpty()) {
                    throw new BusinessRuleException("Le numéro de chèque est obligatoire");
                }
                if (request.getBanqueCheque() == null || request.getBanqueCheque().isEmpty()) {
                    throw new BusinessRuleException("La banque émettrice est obligatoire pour un chèque");
                }
                break;

            case VIREMENT:
                if (request.getReferenceVirement() == null || request.getReferenceVirement().isEmpty()) {
                    throw new BusinessRuleException("La référence de virement est obligatoire");
                }
                if (request.getBanqueVirement() == null || request.getBanqueVirement().isEmpty()) {
                    throw new BusinessRuleException("La banque émettrice est obligatoire pour un virement");
                }
                break;
        }
    }
}
