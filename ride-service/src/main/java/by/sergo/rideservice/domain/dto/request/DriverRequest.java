package by.sergo.rideservice.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequest {
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long driverId;
}
