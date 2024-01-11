package by.sergo.passengerservice.domain.dto;

import lombok.Value;

@Value
public class PassengerCreateUpdateRequestDto {
    String name;
    String surname;
    String email;
    String phone;
}
