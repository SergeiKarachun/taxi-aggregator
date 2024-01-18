package by.sergo.passengerservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingResponse {
    private Long id;
    private Integer grade;
    private Long passengerId;
    private Long driverId;
    private String rideId;
}
