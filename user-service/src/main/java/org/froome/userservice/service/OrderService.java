package org.froome.userservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.userservice.model.Order;
import org.froome.userservice.model.OrderItem;
import org.froome.userservice.model.Payment;
import org.froome.userservice.repository.OrderItemRepository;
import org.froome.userservice.repository.OrderRepository;
import org.froome.userservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public void deleteOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.forEach(order -> deleteOrderItemsByOrderId(order.getId()));
        orders.forEach(order -> deletePaymentsByOrderId(order.getId()));
        orderRepository.deleteAll(orders);
    }

    private void deleteOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        orderItemRepository.deleteAll(orderItems);
    }

    private void deletePaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        paymentRepository.deleteAll(payments);
    }
}
