package com.microtech.smartshop.service;

import com.microtech.smartshop.dto.request.PaymentCreateRequest;
import com.microtech.smartshop.dto.response.PaymentResponse;
import com.microtech.smartshop.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    PaymentResponse addPayment(PaymentCreateRequest request);

    PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatus newStatus);

    List<PaymentResponse> getCommandePayments(Long commandeId);

    PaymentResponse getPayment(Long id);
}
