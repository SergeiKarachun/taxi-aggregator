package by.sergo.passengerservice.domain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PassengerResponseDto {
    Long id;
    String name;
    String surname;
    String email;
    String phone;
    Double rating;
}
