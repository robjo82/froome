package org.froome.userservice.service;

import lombok.RequiredArgsConstructor;
import org.froome.userservice.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;

    public boolean isNotAdmin(String token) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            return !((boolean) claims.get("isAdmin"));
        } else {
            throw new UnauthorizedException("The provided token is not valid.");
        }
    }

    public boolean isNotAdminOrSameUser(String token, long id) {
        if (jwtService.validateToken(token)) {
            Map<String, Object> claims = jwtService.extractAllClaims(token);
            long userId = Long.parseLong(claims.get("id").toString());
            return userId != id && !((boolean) claims.get("isAdmin"));
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
}