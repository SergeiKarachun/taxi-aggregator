package by.sergo.rideservice.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
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
    @NotNull(message = "{payment.not.empty.message}")
    @Pattern(regexp = "CARD|CASH", message = "{invalid.payment.method.message}")
    private String paymentMethod;
}
