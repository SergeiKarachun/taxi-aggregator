package by.sergo.driverservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RatingResponse {
    private Long id;
    private Integer grade;
    private Long driverId;
    private PassengerResponse passenger;
    private RideResponse ride;
}
