package by.sergo.passengerservice.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerCreateUpdateRequest {
    private final static String PHONE_REGEX = "^\\+375(29|33|44|25)(\\d{7})$";
    private final static String EMAIL_EXAMPLE = "example@gmail.com";
    @NotBlank(message = "{name.not.blank}")
    @Size(min = 2, message = "{name.min.value}")
    String name;
    @NotBlank(message = "{email.not.blank}")
    @Schema(example = EMAIL_EXAMPLE)
    @Email(message = "{email.pattern}")
    String email;
    @NotBlank(message = "{surname.not.blank}")
    @Size(min = 2, message = "{surname.min.value}")
    String surname;
    @NotBlank(message = "{phone.not.blank}")
    @Pattern(regexp = PHONE_REGEX, message = "{phone.message.pattern}")
    String phone;
}
