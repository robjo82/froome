package org.froome.orderservice.service;

import org.froome.orderservice.exception.NotFoundException;
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
        removeQuantity(orderItemDto.getProductId(), orderItemDto.getQuantity());

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found")));
        orderItem.setProduct(productRepository.findById(orderItemDto.getProductId()).orElseThrow(() -> new NotFoundException("Product not found")));
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
        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow(() -> new NotFoundException("Order item not found"));
        return toDto(orderItem);
    }

    public OrderItemDto updateItem(Long orderId, Long itemId, OrderItemDto orderItemDto) {
        int oldQuantity = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow(() -> new NotFoundException("Order item not found")).getQuantity();

        updateQuantity(orderItemDto.getProductId(), oldQuantity, orderItemDto.getQuantity());

        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow(() -> new NotFoundException("Order item not found"));
        orderItem.setProduct(productRepository.findById(orderItemDto.getProductId()).orElseThrow(() -> new NotFoundException("Product not found")));
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        orderItem = orderItemRepository.save(orderItem);
        return toDto(orderItem);
    }

    public void deleteItem(Long orderId, Long itemId) {
        addQuantity(orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow(() -> new NotFoundException("Order item not found")).getProduct().getId(), orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow(() -> new NotFoundException("Order item not found")).getQuantity());

        OrderItem orderItem = orderItemRepository.findByOrderIdAndId(orderId, itemId).orElseThrow(() -> new NotFoundException("Order item not found"));
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

    private void removeQuantity(Long productId, int quantity) {
        int oldStock = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found")).getStock();
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found")).setStock(oldStock - quantity);
    }

    private void addQuantity(Long productId, int quantity) {
        int oldStock = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found")).getStock();
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found")).setStock(oldStock + quantity);
    }

    private void updateQuantity(Long productId, int oldQuantity, int newQuantity) {
        int stock = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found")).getStock();
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found")).setStock(stock + oldQuantity - newQuantity);
    }
}

