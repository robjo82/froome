package org.froome.orderservice.service;

import org.froome.orderservice.model.dto.OrderItemDto;
import org.froome.orderservice.model.OrderItem;
import org.froome.orderservice.repository.OrderItemRepository;
import org.froome.orderservice.repository.OrderRepository;
import org.froome.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemDto addItem(Long orderId, OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(orderRepository.findById(orderId).orElseThrow());
        orderItem.setProduct(productRepository.findById(orderItemDto.getProductId()).orElseThrow());
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem = orderItemRepository.save(orderItem);
        return toDto(orderItem);
    }

    public List<OrderItemDto> getAllItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderItemDto getItemById(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow();
        return toDto(orderItem);
    }

    public OrderItemDto updateItem(Long orderId, Long itemId, OrderItemDto orderItemDto) {
        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow();
        orderItem.setProduct(productRepository.findById(orderItemDto.getProductId()).orElseThrow());
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem = orderItemRepository.save(orderItem);
        return toDto(orderItem);
    }

    public void deleteItem(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow();
        orderItemRepository.delete(orderItem);
    }

    private OrderItemDto toDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setProductId(orderItem.getProduct().getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }
}

