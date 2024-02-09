package by.sergo.driverservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
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
