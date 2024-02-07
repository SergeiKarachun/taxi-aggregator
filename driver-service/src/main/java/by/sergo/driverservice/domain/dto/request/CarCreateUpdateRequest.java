package by.sergo.driverservice.domain.dto.request;

import by.sergo.driverservice.util.ConstantUtil;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarCreateUpdateRequest {
    @NotBlank(message = "{model.not.blank}")
    @Size(min = 2, message = "{model.min.value}")
    private String model;
    @NotNull
    @Min(value = 2005, message = "{year.min.value}")
    private Integer yearOfManufacture;
    @NotBlank(message = "{car.number.not.blank}")
    @Pattern(regexp = ConstantUtil.NUMBER_PATTERN, message = "{car.number.message.pattern}")
    private String number;
    @NotBlank(message = "{color.not.blank}")
    @Pattern(regexp = ConstantUtil.COLOR_PATTERN, message = "{color.message}")
    private String color;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long driverId;
}
