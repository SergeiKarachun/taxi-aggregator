package by.sergo.paymentservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private Long driverId;
    private BigDecimal balance;
}
