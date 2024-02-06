package by.sergo.rideservice.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditDriverStatusRequest {
    private Long driverId;
}
