package by.sergo.driverservice.mapper;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.domain.entity.Driver;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverMapper {
    private final ModelMapper modelMapper;

    public Driver mapToEntity(DriverCreateUpdateRequest requestDto) {
        return modelMapper.map(requestDto, Driver.class);
    }

    public DriverResponse mapToDto(Driver driver) {
        return modelMapper.map(driver, DriverResponse.class);
    }
}
