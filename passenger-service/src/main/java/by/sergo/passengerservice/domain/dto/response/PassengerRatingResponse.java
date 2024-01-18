package by.sergo.passengerservice.domain.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
public class PassengerRatingResponse {
    private Long passengerId;
    private Double rating;
}
