package by.sergo.rideservice.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCreateUpdateRequestDto {
    String pickUpAddress;
    String destinationAddress;
    Double price;
    Long passengerId;
}
