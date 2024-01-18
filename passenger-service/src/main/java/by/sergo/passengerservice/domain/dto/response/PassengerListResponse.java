package by.sergo.passengerservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class PassengerListResponse {
    private Integer page;
    private Integer totalPages;
    private Integer size;
    private Integer total;
    private String sortedByField;
    private List<PassengerResponse> passengers;
}
