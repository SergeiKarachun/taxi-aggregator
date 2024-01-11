package by.sergo.rideservice.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCreateUpdateRequestDto {
    @NotBlank(message = "Pick up address is required")
    @Size(min = 2, message = "Pick up address should have at least 4 characters")
    String pickUpAddress;
    @NotBlank(message = "Destination address is required")
    @Size(min = 2, message = "Destination address should have at least 4 characters")
    String destinationAddress;
    @NotNull
    @Min(value = 1, message = "Min value is 1")
    Long passengerId;
}
