package by.sergo.driverservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CarResponse {
    private Long id;
    private String model;
    private Integer yearOfManufacture;
    private String number;
    private String color;
    private Long driverId;
}
