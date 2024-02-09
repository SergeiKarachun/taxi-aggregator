package by.sergo.paymentservice.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static by.sergo.paymentservice.util.ConstantUtils.MIN_PRICE;

@Getter
@Setter
@Builder
public class PaymentRequest {
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long driverId;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long passengerId;
    @NotBlank(message = "{ride.id.not.blank}")
    private String rideId;
    @DecimalMin(value = MIN_PRICE, inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal sum;
}
