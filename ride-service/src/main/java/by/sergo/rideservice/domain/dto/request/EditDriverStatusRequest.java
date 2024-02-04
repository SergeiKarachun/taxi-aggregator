package by.sergo.rideservice.domain.dto.request;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EditDriverStatusRequest {
    private Long driverId;
}
