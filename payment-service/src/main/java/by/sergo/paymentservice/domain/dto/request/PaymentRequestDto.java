package by.sergo.paymentservice.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDto {
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long driverId;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long passengerId;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long rideId;
    @DecimalMin(value = "2.70", inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal sum;
}
