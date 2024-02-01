package by.sergo.passengerservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RatingResponse {
    private Long id;
    private Integer grade;
    private Long passengerId;
    private DriverResponse driver;
    private RideResponse ride;
}
