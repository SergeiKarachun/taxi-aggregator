package by.sergo.passengerservice.domain.dto;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PassengerRatingResponseDto {
    Long passengerId;
    Double rating;
}
