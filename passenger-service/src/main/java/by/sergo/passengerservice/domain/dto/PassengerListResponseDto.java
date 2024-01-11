package by.sergo.passengerservice.domain.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PassengerListResponseDto {
    Integer page;
    Integer size;
    Integer total;
    String sortedByField;
    List<PassengerResponseDto> passengers;
}
