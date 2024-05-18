package org.froome.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.orderservice.exception.UnauthorizedException;
import org.froome.orderservice.model.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;

    public boolean isNotAdmin(String token) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            return (!((boolean) claims.get("isntAdmin")));
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
}