package org.froome.orderservice.controller;

import org.froome.orderservice.exception.ForbiddenException;
import org.froome.orderservice.model.OrderStatus;
import org.froome.orderservice.model.dto.ExceptionDto;
import org.froome.orderservice.model.dto.OrderDto;
import org.froome.orderservice.service.AuthService;
import org.froome.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Order", description = "Specific to orders, manages the creation, update, and deletion of orders.")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    @PostMapping
    @Operation(
            summary = "Create a new order",
            description = "Create a new order for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderDto> create(
            @Valid @RequestBody OrderDto orderDto,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        long userId = authService.getUserIdFromToken(token);
        OrderDto createdOrder = orderService.createOrder(orderDto, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get all orders",
            description = "Only admins can get all orders.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<OrderDto>> getAll(
            @RequestParam(required = false, name = "page", defaultValue = "0") Integer page,
            @RequestParam(required = false, name = "size", defaultValue = "10") Integer size,
            Authentication authentication
    ) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotAdmin(token)) {
            throw new ForbiddenException("You are not allowed to view all orders.");
        }
        List<OrderDto> orders = orderService.getAllOrders(page, size);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an order by ID",
            description = "Get an order by its ID. Admins and the user associated with the order can access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderDto> get(
            @Parameter(description = "ID of the order to be retrieved", required = true)
            @PathVariable long id,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        OrderDto order = orderService.getOrderById(id);
        if (authService.isNotAdmin(token) && authService.isNotUserAssociatedWithOrder(token, order)) {
            throw new ForbiddenException("You are not allowed to view this order.");
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/mine")
    @Operation(
            summary = "Get orders by user ID",
            description = "Get all orders associated with a user ID. Admins and the user can access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<OrderDto>> getByUserId(
            Authentication authentication) {
        return new ResponseEntity<>(
                orderService.getOrdersByUserId(
                        authService.getUserIdFromToken(
                                authentication.getPrincipal().toString())
                ),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update the status of an order by ID",
            description = "Update the status of an order by its ID. Admins and the user associated with the order can update.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order status updated"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderDto> updateStatus(
            @Parameter(description = "ID of the order to update the status for", required = true)
            @PathVariable long id,
            @Parameter(description = "New status of the order", required = true)
            @RequestParam OrderStatus status,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        OrderDto existingOrder = orderService.getOrderById(id);
        if (authService.isNotAdmin(token) && authService.isNotUserAssociatedWithOrder(token, existingOrder)) {
            throw new ForbiddenException("You are not allowed to update the status of this order.");
        }
        OrderDto updatedOrder = orderService.updateOrderStatus(id, status);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an order by ID",
            description = "Delete an order by its ID. Admins and the user associated with the order can delete.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order deleted"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the order to be deleted", required = true)
            @PathVariable long id,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        OrderDto existingOrder = orderService.getOrderById(id);
        if (authService.isNotAdmin(token) && authService.isNotUserAssociatedWithOrder(token, existingOrder)) {
            throw new ForbiddenException("You are not allowed to delete this order.");
        }
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
