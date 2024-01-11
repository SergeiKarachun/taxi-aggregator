package by.sergo.rideservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RideListResponseDto {
    Integer page;
    Integer totalPages;
    Integer size;
    Integer total;
    String sortedByField;
    private List<RideResponseDto> rides;
}
