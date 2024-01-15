package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerRatingResponse;
import by.sergo.passengerservice.domain.dto.response.RatingResponse;

public interface RatingService {
    RatingResponse createRateOfPassenger(RatingCreateRequest dto, Long passengerId);
    PassengerRatingResponse getPassengerRating(Long passengerId);
}
