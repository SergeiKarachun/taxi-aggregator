package by.sergo.paymentservice.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static by.sergo.paymentservice.util.ConstantUtils.MIN_BALANCE;

@Getter
@Setter
@Builder
public class AccountCreateUpdateRequest {
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long driverId;
    @DecimalMin(value = MIN_BALANCE, inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal balance;
}
