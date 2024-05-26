package org.froome.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.orderservice.exception.UnauthorizedException;
import org.froome.orderservice.model.Order;
import org.froome.orderservice.model.dto.OrderDto;
import org.froome.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final OrderRepository orderRepository;

    public boolean isNotAdmin(String token) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            return (!((boolean) claims.get("isAdmin")));
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }


    public long getUserIdFromToken(String token) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            return Long.parseLong(claims.get("id").toString());
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }

    public boolean isNotUserAssociatedWithOrder(String token, OrderDto order) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            long userId = Long.parseLong(claims.get("id").toString());
            return userId != order.getUserId();
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }

    public boolean isNotUserAssociatedWithOrder(String token, Long orderId) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            String userId = claims.get("id").toString();
            Order order = orderRepository.findById(orderId).orElseThrow();
            return !Objects.equals(userId, order.getUser().getId().toString());
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }
}