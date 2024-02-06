package by.sergo.paymentservice.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Double rating;
}
