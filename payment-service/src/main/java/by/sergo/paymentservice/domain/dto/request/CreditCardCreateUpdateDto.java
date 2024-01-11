package by.sergo.paymentservice.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreditCardCreateUpdateDto {
    private String creditCardNumber;
    private String cvv;
    private LocalDate expDate;
    private Long userId;
    private BigDecimal balance;
    private String userType;
}
