package by.sergo.paymentservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreditCardResponse {
    private Long id;
    private String creditCardNumber;
    private String cvv;
    private LocalDate expDate;
    private BigDecimal balance;
    private String userType;
    private UserResponse userResponse;
}
