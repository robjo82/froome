package org.froome.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.orderservice.exception.NotFoundException;
import org.froome.orderservice.model.Order;
import org.froome.orderservice.model.OrderStatus;
import org.froome.orderservice.model.dto.OrderDto;
import org.froome.orderservice.repository.OrderItemRepository;
import org.froome.orderservice.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;

    public OrderDto createOrder(OrderDto orderDto, long userId) {
        Order order = modelMapper.map(orderDto, Order.class);
        order.setUser(userService.getUserById(userId));
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDto.class);
    }

    public List<OrderDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        return modelMapper.map(order, OrderDto.class);
    }
    
    public List<OrderDto> getOrdersByUserId(long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    public OrderDto updateOrderStatus(long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(status.getValue());
        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderDto.class);
    }

    public void deleteOrder(long id) {
        orderItemRepository.findByOrderId(id).forEach(orderItem -> orderItemService.deleteItem(id, orderItem.getId()));
        orderRepository.deleteById(id);
    }
}
