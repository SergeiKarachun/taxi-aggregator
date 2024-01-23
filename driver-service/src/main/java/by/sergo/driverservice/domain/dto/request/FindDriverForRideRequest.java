package by.sergo.driverservice.domain.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FindDriverForRideRequest {
    private String rideId;
}
