package by.sergo.rideservice.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequestDto {
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    private Long driverId;
}
