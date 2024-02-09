package by.sergo.paymentservice.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStoreResponse {
    private Long id;
    private String creditCardNumber;
    private String accountNumber;
    private LocalDateTime operationDate;
    private String operation;
    private BigDecimal value;
}
