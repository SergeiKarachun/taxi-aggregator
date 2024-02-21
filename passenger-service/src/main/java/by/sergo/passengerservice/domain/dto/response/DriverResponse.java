package by.sergo.passengerservice.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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
