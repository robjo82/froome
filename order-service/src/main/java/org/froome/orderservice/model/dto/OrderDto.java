package org.froome.orderservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.froome.orderservice.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.froome.orderservice.model.OrderStatus.CREATED;


@Data
public class OrderDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime orderDate;

    @NotBlank
    @Schema(defaultValue = "CREATED")
    private OrderStatus status = CREATED;

    @NotBlank
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long userId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<OrderItemDto> orderItems;
}

