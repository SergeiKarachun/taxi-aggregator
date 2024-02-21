package by.sergo.driverservice.domain.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DriverForRideResponse {
    private Long driverId;
    private String rideId;
}

