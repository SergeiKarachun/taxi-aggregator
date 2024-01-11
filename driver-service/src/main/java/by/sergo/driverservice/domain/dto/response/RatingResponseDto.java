package by.sergo.driverservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingResponseDto {
    private Long id;
    private Integer grade;
    private Long driverId;
    private Long passengerId;
}
