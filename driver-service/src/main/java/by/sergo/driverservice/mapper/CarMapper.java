package by.sergo.driverservice.mapper;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.domain.entity.Car;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarMapper {
    private final ModelMapper modelMapper;

    public CarResponse mapToDto(Car car) {
        return modelMapper.map(car, CarResponse.class);
    }

    public Car mapToEntity(CarCreateUpdateRequest dto) {
        return modelMapper.map(dto, Car.class);
    }
}
