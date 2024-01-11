package by.sergo.paymentservice.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionStoreResponseDto {
    private Long id;
    private String creditCardNumber;
    private String accountNumber;
    private LocalDateTime operationDate;
    private String operation;
    private BigDecimal value;
}
