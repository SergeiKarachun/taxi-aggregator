package by.sergo.driverservice.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class CarCreateUpdateRequestDto {
    @NotBlank(message = "Model is required")
    @Size(min = 2, message = "Model should have at least 2 characters")
    private String model;
    @NotNull
    @Min(value = 2005, message = "Min value is 2005")
    private Integer yearOfManufacture;
    @NotBlank(message = "Car number is required")
    @Pattern(regexp = "^\\d{4} [A-Z]{2}-\\d{1}$", message = "Number pattern is 1111 AA-1.")
    private String number;
    @NotBlank(message = "Color is required")
    @Pattern(regexp = "^(RED|BLACK|WHITE|BLUE|SILVER|YELLOW|GREEN)$", message = "Select color:RED,BLACK,WHITE,BLUE,SILVER,YELLOW,GREEN.")
    private String color;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long driverId;
}
