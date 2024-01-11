package by.sergo.driverservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarResponseDto {
    private Long id;
    private String model;
    private Integer yearOfManufacture;
    private String number;
    private String color;
    private Long driverId;
}
