package by.sergo.rideservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Double rating;
}
