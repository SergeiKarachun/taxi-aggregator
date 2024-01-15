package by.sergo.driverservice.domain.dto.request;

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
public class DriverCreateUpdateRequest {
    private static final String EMAIL_EXAMPLE = "example@gmail.com";
    private static final String PHONE_PATTERN = "^\\+375(29|33|44|25)(\\d{7})$";
    @NotBlank(message = "{name.not.blank}")
    @Size(min = 2, message = "{name.min.value}")
    String name;
    @NotBlank(message = "{surname.not.blank}")
    @Size(min = 2, message = "{surname.min.value}")
    String surname;
    @NotBlank(message = "{email.not.blank}")
    @Schema(example = EMAIL_EXAMPLE)
    @Email(message = "{email.pattern}")
    String email;
    @NotBlank(message = "{phone.not.blank}")
    @Pattern(regexp = PHONE_PATTERN, message = "{phone.message.pattern}")
    String phone;

}
