package org.froome.paymentservice.service;

import org.froome.paymentservice.exception.NotFoundException;
import org.froome.paymentservice.model.Order;
import org.froome.paymentservice.model.OrderItem;
import org.froome.paymentservice.model.OrderStatus;
import org.froome.paymentservice.model.Payment;
import org.froome.paymentservice.model.dto.PaymentDto;
import org.froome.paymentservice.repository.PaymentRepository;
import org.froome.paymentservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentDto createPayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        switch (OrderStatus.valueOf(order.getStatus())) {
            case CREATED:
                break;
            case PAID:
                throw new NotFoundException("Order is already paid");
            case SHIPPED:
                throw new NotFoundException("Order is already shipped");
            case DELIVERED:
                throw new NotFoundException("Order is already delivered");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setAmount(getTotalAmount(orderId));
        payment = paymentRepository.save(payment);

        order.setStatus("PAID");
        orderRepository.save(order);

        return toDto(payment);
    }

    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment not found"));
        return toDto(payment);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment not found"));

        Order order = payment.getOrder();
        order.setStatus("CREATED");
        orderRepository.save(order);

        paymentRepository.delete(payment);
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmount(payment.getAmount());
        return dto;
    }

    private BigDecimal getTotalAmount(Long orderId) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found")).getOrderItems()) {
            totalAmount = totalAmount.add(
                    BigDecimal.valueOf(orderItem.getQuantity()).multiply(
                            BigDecimal.valueOf(orderItem.getProduct().getPrice())
                    ));
        }

        return totalAmount;
    }
}
