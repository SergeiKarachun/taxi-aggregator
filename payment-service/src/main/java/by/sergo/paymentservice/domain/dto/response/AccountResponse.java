package by.sergo.paymentservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Long driverId;
    private BigDecimal balance;
}
