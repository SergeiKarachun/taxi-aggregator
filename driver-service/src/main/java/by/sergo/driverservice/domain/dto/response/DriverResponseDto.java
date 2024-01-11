package by.sergo.driverservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverResponseDto {
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String status;
    private Double rating;
    private CarResponseDto car;
}
