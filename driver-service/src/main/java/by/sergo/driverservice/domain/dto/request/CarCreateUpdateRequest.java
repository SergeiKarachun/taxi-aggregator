package by.sergo.driverservice.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCreateUpdateRequest {
    private static final String NUMBER_PATTERN = "^\\d{4} [A-Z]{2}-\\d{1}$";
    private static final String COLOR_PATTERN = "^(RED|BLACK|WHITE|BLUE|SILVER|YELLOW|GREEN)$";
    @NotBlank(message = "{model.not.blank}")
    @Size(min = 2, message = "{model.min.value}")
    private String model;
    @NotNull
    @Min(value = 2005, message = "{year.min.value}")
    private Integer yearOfManufacture;
    @NotBlank(message = "{car.number.not.blank}")
    @Pattern(regexp = NUMBER_PATTERN, message = "{car.number.message.pattern}")
    private String number;
    @NotBlank(message = "{color.not.blank}")
    @Pattern(regexp = COLOR_PATTERN, message = "{color.message}")
    private String color;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long driverId;
}
