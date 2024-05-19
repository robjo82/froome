package org.froome.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.paymentservice.exception.NotFoundException;
import org.froome.paymentservice.exception.UnauthorizedException;
import org.froome.paymentservice.model.Order;
import org.froome.paymentservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final OrderRepository orderRepository;


    public boolean isNotUserAssociatedWithOrder(String token, Long orderId) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            String userId = claims.get("id").toString();
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
            return !Objects.equals(userId, order.getUser().getId().toString());
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }

    public boolean isNotAdmin(String token) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            return (!((boolean) claims.get("isAdmin")));
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }
}