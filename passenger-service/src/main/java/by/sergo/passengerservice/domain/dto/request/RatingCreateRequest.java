package by.sergo.passengerservice.domain.dto.request;

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
public class RatingCreateRequest {
    @NotNull
    @Min(value = 1, message = "{min.value}")
    @Max(value = 5, message = "{max.value}")
    Integer grade;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    Long driverId;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    Long rideId;
}
