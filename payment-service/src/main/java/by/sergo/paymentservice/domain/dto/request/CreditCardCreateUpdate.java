package by.sergo.paymentservice.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static by.sergo.paymentservice.util.ConstantUtils.*;

@Getter
@Setter
public class CreditCardCreateUpdate {

    @NotBlank(message = "{credit.card.not.blank}")
    @Pattern(regexp = CREDIT_CARD_NUMBER_PATTERN, message = "{credit.card.number.pattern}")
    private String creditCardNumber;
    @NotBlank(message = "{cvv.card.not.blank}")
    @Pattern(regexp = CVV_PATTERN, message = "{cvv.pattern}")
    private String cvv;
    @Future(message = "{expiration.date.message}")
    private LocalDate expDate;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long userId;
    @DecimalMin(value = MIN_BALANCE, inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal balance;
    @NotBlank(message = "{user.type.not.blank}")
    @Pattern(regexp = USER_TYPE_PATTERN, message = "{user.type.message}")
    private String userType;
}
