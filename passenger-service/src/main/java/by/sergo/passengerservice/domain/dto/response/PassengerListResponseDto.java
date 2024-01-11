package by.sergo.passengerservice.domain.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassengerListResponseDto {
    Integer page;
    Integer totalPages;
    Integer size;
    Integer total;
    String sortedByField;
    List<PassengerResponseDto> passengers;
}
