package by.sergo.driverservice.domain.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverCreateUpdateRequestDto {

    String name;
    String surname;
    String email;
    String phone;

}
