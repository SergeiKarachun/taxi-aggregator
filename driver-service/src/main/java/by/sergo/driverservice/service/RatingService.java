package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;

public interface RatingService {
    RatingResponse createRateOfDriver(RatingCreateRequest dto, Long driverId);
    DriverRatingResponse getDriverRating(Long driverId);
}
