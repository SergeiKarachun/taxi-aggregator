package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.request.FindDriverForRideRequest;
import by.sergo.driverservice.domain.dto.response.DriverListResponse;
import by.sergo.driverservice.domain.dto.response.DriverResponse;

public interface DriverService {
    DriverResponse create(DriverCreateUpdateRequest dto);

    DriverResponse update(Long id, DriverCreateUpdateRequest dto);

    DriverResponse delete(Long id);

    DriverResponse getById(Long id);

    DriverListResponse getAvailableDrivers(Integer page, Integer size, String orderBy);

    DriverListResponse getAll(Integer page, Integer size, String orderBy);

    DriverResponse changeStatus(Long id);

    void handleDriverForRide(FindDriverForRideRequest request);
}
