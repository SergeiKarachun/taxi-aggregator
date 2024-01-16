package by.sergo.passengerservice.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingCreateRequest {
    @NotNull
    @Min(value = 1, message = "{min.value}")
    @Max(value = 5, message = "{max.value}")
    private Integer grade;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long driverId;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long rideId;
}
