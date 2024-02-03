package by.sergo.driverservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PassengerResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Double rating;
}
