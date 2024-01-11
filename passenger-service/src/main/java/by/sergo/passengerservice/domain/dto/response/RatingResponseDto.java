package by.sergo.passengerservice.domain.dto.response;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RatingResponseDto {
    Long id;
    Integer grade;
    Long passengerId;
    Long driverId;
}
