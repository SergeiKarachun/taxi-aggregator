package by.sergo.driverservice.domain.dto.request;

import by.sergo.driverservice.util.ConstantUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DriverCreateUpdateRequest {
    @NotBlank(message = "{name.not.blank}")
    @Size(min = 2, message = "{name.min.value}")
    private String name;
    @NotBlank(message = "{surname.not.blank}")
    @Size(min = 2, message = "{surname.min.value}")
    private String surname;
    @NotBlank(message = "{email.not.blank}")
    @Schema(example = ConstantUtil.EMAIL_EXAMPLE)
    @Email(message = "{email.pattern}")
    private String email;
    @NotBlank(message = "{phone.not.blank}")
    @Pattern(regexp = ConstantUtil.PHONE_PATTERN, message = "{phone.message.pattern}")
    private String phone;

}
