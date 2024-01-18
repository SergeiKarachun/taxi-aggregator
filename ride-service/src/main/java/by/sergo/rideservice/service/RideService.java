package by.sergo.rideservice.service;

import by.sergo.rideservice.domain.dto.request.DriverRequest;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.RideListResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;

public interface RideService {
    RideResponse create(RideCreateUpdateRequest dto);
    RideResponse getById(String id);
    RideResponse deleteById(String id);
    RideResponse update(RideCreateUpdateRequest dto, String id);
    RideResponse setDriverAndAcceptRide(DriverRequest dto, String rideId);
    RideResponse rejectRide(String rideId);
    RideResponse startRide(String rideId);
    RideResponse endRide(String rideId);
    RideListResponse getByPassengerId(Long passengerId, String status, Integer page, Integer size, String orderBy);
    RideListResponse getByDriverId(Long driverId, String status, Integer page, Integer size, String orderBy);
}
