package by.sergo.passengerservice.domain.dto;

import lombok.Value;

@Value
public class RatingCreateRequestDto {
    Integer grade;
    Long driverId;
}
