package by.sergo.paymentservice.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountCreateUpdateRequestDto {
    private Long driverId;
    private BigDecimal balance;
}
