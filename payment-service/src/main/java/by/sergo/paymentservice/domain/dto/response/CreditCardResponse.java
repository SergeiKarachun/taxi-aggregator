package by.sergo.paymentservice.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreditCardResponse {
    private Long id;
    private String creditCardNumber;
    private String cvv;
    private LocalDate expDate;
    private BigDecimal balance;
    private String userType;
    private UserResponse user;
}
