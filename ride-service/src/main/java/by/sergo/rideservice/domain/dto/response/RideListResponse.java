package by.sergo.rideservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RideListResponse {
    private Integer totalPages;
    private Integer size;
    private Integer total;
    private String sortedByField;
    private List<RideResponse> rides;
}
