package by.sergo.driverservice.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingCreateRequestDto {
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    @Max(value = 5, message = "Max value is 5")
    Integer grade;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    Long passengerId;
}
