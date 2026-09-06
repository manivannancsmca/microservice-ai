package com.example.payment.service;

import com.example.payment.dto.request.CreatePaymentRequest;
import com.example.payment.dto.request.UpdatePaymentStatusRequest;
import com.example.payment.dto.response.PaymentResponse;
import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.exception.BusinessException;
import com.example.payment.exception.InvalidRequestException;
import com.example.payment.exception.ResourceNotFoundException;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentResponse create(CreatePaymentRequest request) {
        log.info("Creating payment for orderId={}", request.orderId());

        Payment payment = paymentMapper.toEntity(request);
        payment = paymentRepository.save(payment);

        log.info("Payment created: paymentId={}", payment.getPaymentId());
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByPaymentId(String paymentId) {
        Payment payment = findEntityByPaymentId(paymentId);
        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> search(String orderId, PaymentStatus status, Pageable pageable) {
        Page<Payment> page;
        if (orderId != null && status != null) {
            page = paymentRepository.findByOrderIdAndStatus(orderId, status, pageable);
        } else if (orderId != null) {
            page = paymentRepository.findByOrderId(orderId, pageable);
        } else if (status != null) {
            page = paymentRepository.findByStatus(status, pageable);
        } else {
            page = paymentRepository.findAll(pageable);
        }
        return page.map(paymentMapper::toResponse);
    }

    public PaymentResponse updateStatus(String paymentId, UpdatePaymentStatusRequest request) {
        Payment payment = findEntityByPaymentId(paymentId);
        validateStatusTransition(payment.getStatus(), request.status());

        payment.setStatus(request.status());
        if (request.transactionRef() != null) {
            payment.setTransactionRef(request.transactionRef());
        }
        if (request.failureReason() != null) {
            payment.setFailureReason(request.failureReason());
        }

        payment = paymentRepository.save(payment);
        log.info("Payment {} status changed to {}", paymentId, request.status());
        return paymentMapper.toResponse(payment);
    }

    private Payment findEntityByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
    }

    private void validateStatusTransition(PaymentStatus current, PaymentStatus next) {
        // Simple but clear state machine – expand later if needed
        boolean allowed = switch (current) {
            case INITIATED -> next == PaymentStatus.PROCESSING || next == PaymentStatus.CANCELLED || next == PaymentStatus.FAILED;
            case PROCESSING -> next == PaymentStatus.COMPLETED || next == PaymentStatus.FAILED;
            case COMPLETED -> next == PaymentStatus.REFUNDED;
            case FAILED, CANCELLED, REFUNDED -> false;
        };
        if (!allowed) {
            throw new BusinessException("Invalid status transition from " + current + " to " + next);
        }
    }
}