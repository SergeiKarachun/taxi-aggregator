package by.sergo.paymentservice.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Long driverId;
    private BigDecimal balance;
}
