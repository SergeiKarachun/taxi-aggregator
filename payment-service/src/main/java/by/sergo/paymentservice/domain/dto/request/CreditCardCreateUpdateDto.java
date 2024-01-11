package by.sergo.paymentservice.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreditCardCreateUpdateDto {
    @NotBlank(message = "Credit card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Credit card number pattern is 00000000000000.")
    private String creditCardNumber;
    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3}$", message = "CVV pattern is XXX.")
    private String cvv;
    @Future(message = "Expiration date must be future, format yyyy-MM-dd 2039-01-25")
    private LocalDate expDate;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long userId;
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal balance;
    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^DRIVER|PASSENGER$", message = "User type is DRIVER or PASSENGER.")
    private String userType;
}
