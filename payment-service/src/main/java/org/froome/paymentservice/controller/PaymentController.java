package org.froome.paymentservice.controller;

import org.froome.paymentservice.model.dto.PaymentDto;
import org.froome.paymentservice.service.PaymentService;
import org.froome.paymentservice.service.AuthService;
import org.froome.paymentservice.exception.ForbiddenException;
import org.froome.paymentservice.model.dto.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Specific to payments, manages the creation, retrieval, and deletion of payments.")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;

    @PostMapping
    @Operation(
            summary = "Create a new payment",
            description = "Create a new payment for an order.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Payment created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PaymentDto> createPayment(
            @Parameter(description = "ID of the order", required = true)
            @RequestParam Long orderId,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to make a payment for this order.");
        }
        PaymentDto createdPayment = paymentService.createPayment(orderId);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get payments by order ID",
            description = "Get all payments associated with a specific order ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payments found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PaymentDto>> getPaymentsByOrderId(
            @Parameter(description = "ID of the order", required = true)
            @RequestParam Long orderId,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to view payments for this order.");
        }
        List<PaymentDto> payments = paymentService.getPaymentsByOrderId(orderId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a payment by ID",
            description = "Get a payment by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PaymentDto> getPaymentById(
            @Parameter(description = "ID of the payment", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        PaymentDto payment = paymentService.getPaymentById(id);
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, payment.getOrderId())) {
            throw new ForbiddenException("You are not allowed to view this payment.");
        }
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a payment by ID",
            description = "Delete a payment by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Payment deleted"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "ID of the payment", required = true)
            @PathVariable Long id,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdmin(token)) {
            throw new ForbiddenException("You are not allowed to delete this payment.");
        }
        paymentService.deletePayment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

