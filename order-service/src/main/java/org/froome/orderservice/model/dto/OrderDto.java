package org.froome.orderservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.froome.orderservice.model.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime orderDate;

    @NotBlank
    private OrderStatus status;

    @NotBlank
    private long userId;
}

