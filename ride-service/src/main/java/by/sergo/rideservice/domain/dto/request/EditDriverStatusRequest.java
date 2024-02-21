package by.sergo.rideservice.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EditDriverStatusRequest {
    private Long driverId;
}
