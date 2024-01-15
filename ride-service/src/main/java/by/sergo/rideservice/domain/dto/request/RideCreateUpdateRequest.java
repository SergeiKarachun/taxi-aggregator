package by.sergo.rideservice.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCreateUpdateRequest {
    @NotBlank(message = "{pick.up.address.not.blank}")
    @Size(min = 2, message = "{address.size}")
    private String pickUpAddress;
    @NotBlank(message = "{destination.address.not.blank}")
    @Size(min = 2, message = "{address.size}")
    private String destinationAddress;
    @NotNull
    @Min(value = 1, message = "{min.value}")
    private Long passengerId;
}
