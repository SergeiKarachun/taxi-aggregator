package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarListResponse;
import by.sergo.driverservice.domain.dto.response.CarResponse;

public interface CarService {
    CarResponse create(CarCreateUpdateRequest dto);

    CarResponse update(Long id, CarCreateUpdateRequest dto);

    CarResponse delete(Long id);

    CarResponse getById(Long id);

    CarResponse getByDriverId(Long driverId);

    CarListResponse getAll(Integer page, Integer size, String orderBy);
}
