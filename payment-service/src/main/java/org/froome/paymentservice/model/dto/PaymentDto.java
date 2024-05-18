package org.froome.paymentservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;
    private Long orderId;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
}
