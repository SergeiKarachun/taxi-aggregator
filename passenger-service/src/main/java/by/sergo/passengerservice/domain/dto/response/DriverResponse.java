package by.sergo.passengerservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    private BigDecimal balance;
    private CarResponse car;
}
