package by.sergo.rideservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverResponse {
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String status;
    private Double rating;
    private CarResponse car;
}
