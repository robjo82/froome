package org.froome.orderservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderItemDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long orderId;
    private Long productId;
    private Integer quantity;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private float price;
}

