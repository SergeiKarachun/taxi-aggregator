package by.sergo.driverservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DriverRatingResponseDto {
    private Long driverId;
    private Double rating;
}
