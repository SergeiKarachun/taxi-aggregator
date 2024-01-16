package by.sergo.passengerservice.domain.dto.request;

import by.sergo.passengerservice.domain.dto.ConstantUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerCreateUpdateRequest {
    @NotBlank(message = "{name.not.blank}")
    @Size(min = 2, message = "{name.min.value}")
    private String name;
    @NotBlank(message = "{email.not.blank}")
    @Schema(example = ConstantUtil.EMAIL_EXAMPLE)
    @Email(message = "{email.pattern}")
    private String email;
    @NotBlank(message = "{surname.not.blank}")
    @Size(min = 2, message = "{surname.min.value}")
    private String surname;
    @NotBlank(message = "{phone.not.blank}")
    @Pattern(regexp = ConstantUtil.PHONE_REGEX, message = "{phone.message.pattern}")
    private String phone;
}
