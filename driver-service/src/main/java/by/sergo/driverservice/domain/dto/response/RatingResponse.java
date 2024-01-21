package by.sergo.driverservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingResponse {
    private Long id;
    private Integer grade;
    private Long driverId;
    private PassengerResponse passengerResponse;
    private RideResponse rideResponse;
}
