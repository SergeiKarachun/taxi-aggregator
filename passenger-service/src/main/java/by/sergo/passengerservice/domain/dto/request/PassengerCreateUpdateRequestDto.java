package by.sergo.passengerservice.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

@Value
public class PassengerCreateUpdateRequestDto {
    @NotBlank(message = "Name is required")
    String name;
    @NotBlank(message = "Surname is required")
    String surname;
    @NotBlank(message = "Email is required")
    @Email
    String email;
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "(\\+?(375|80)?\\s?)?\\(?(17|29|33|44|25)\\)?\\s?(\\d{3})[-|\\s]?(\\d{2})[-|\\s]?(\\d{2})", message = "Phone pattern is +37533 1234567. Valid operator codes 25,29,33,44")
    String phone;
}
