package by.sergo.driverservice.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class CarListResponseDto {
    private Integer page;
    private Integer totalPages;
    private Integer size;
    private Integer total;
    private String sortedByField;
    private List<CarResponseDto> cars;
}
