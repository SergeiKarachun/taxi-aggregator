package by.sergo.rideservice.domain.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DriverForRideResponse {
    private Long driverId;
    private String rideId;
}
