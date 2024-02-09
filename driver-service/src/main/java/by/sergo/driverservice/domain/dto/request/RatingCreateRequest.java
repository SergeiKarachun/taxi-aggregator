package by.sergo.driverservice.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RatingCreateRequest {
    @NotNull
    @Min(value = 1, message = "{min.value}")
    @Max(value = 5, message = "{max.value}")
    private Integer grade;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long passengerId;
    @NotBlank(message = "{ride.id.not.blank}")
    private String rideId;
}
