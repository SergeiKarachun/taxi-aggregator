package by.sergo.rideservice.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FindDriverForRideRequest {
    private String rideId;
}
