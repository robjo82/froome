package org.froome.orderservice.controller;

import org.froome.orderservice.exception.ForbiddenException;
import org.froome.orderservice.model.dto.ExceptionDto;
import org.froome.orderservice.model.dto.OrderItemDto;
import org.froome.orderservice.service.AuthService;
import org.froome.orderservice.service.OrderItemService;
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
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
@Validated
@Tag(name = "OrderItem", description = "Specific to order items, manages the creation, update, and deletion of order items.")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final AuthService authService;

    @PostMapping
    @Operation(
            summary = "Add an item to an order",
            description = "Add a new item to an existing order.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order item created"),
                    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderItemDto> addItem(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable long orderId,
            @Valid @RequestBody OrderItemDto orderItemDto,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to add items to this order.");
        }
        OrderItemDto createdOrderItem = orderItemService.addItem(orderId, orderItemDto);
        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get all items of an order",
            description = "Get all items of an order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order items found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<OrderItemDto>> getAllItems(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable long orderId,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to view items of this order.");
        }
        List<OrderItemDto> orderItems = orderItemService.getAllItems(orderId);
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    @Operation(
            summary = "Get an order item by ID",
            description = "Get an order item by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order item found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order item not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderItemDto> getItem(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable long orderId,
            @Parameter(description = "ID of the order item", required = true)
            @PathVariable long itemId,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to view this order item.");
        }
        OrderItemDto orderItem = orderItemService.getItemById(orderId, itemId);
        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }

    @PutMapping("/{itemId}")
    @Operation(
            summary = "Update an order item by ID",
            description = "Update an order item by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order item updated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order item not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<OrderItemDto> updateItem(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable long orderId,
            @Parameter(description = "ID of the order item", required = true)
            @PathVariable long itemId,
            @Valid @RequestBody OrderItemDto orderItemDto,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to update this order item.");
        }
        OrderItemDto updatedOrderItem = orderItemService.updateItem(orderId, itemId, orderItemDto);
        return new ResponseEntity<>(updatedOrderItem, HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    @Operation(
            summary = "Delete an order item by ID",
            description = "Delete an order item by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order item deleted"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order item not found", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable long orderId,
            @Parameter(description = "ID of the order item", required = true)
            @PathVariable long itemId,
            Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        if (authService.isNotUserAssociatedWithOrder(token, orderId)) {
            throw new ForbiddenException("You are not allowed to delete this order item.");
        }
        orderItemService.deleteItem(orderId, itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

