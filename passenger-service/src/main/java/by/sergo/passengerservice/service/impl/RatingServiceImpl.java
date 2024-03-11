package by.sergo.passengerservice.service.impl;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.*;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.mapper.RatingMapper;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.repository.RatingRepository;
import by.sergo.passengerservice.service.DriverService;
import by.sergo.passengerservice.service.RatingService;
import by.sergo.passengerservice.service.RideService;
import by.sergo.passengerservice.service.exception.BadRequestException;
import by.sergo.passengerservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static by.sergo.passengerservice.util.ConstantUtil.*;
import static by.sergo.passengerservice.util.ExceptionMessageUtil.getAlreadyExistMessage;
import static by.sergo.passengerservice.util.ExceptionMessageUtil.getNotFoundMessage;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;
    private final DriverService driverService;
    private final RideService rideService;

    @Override
    @Transactional
    public RatingResponse ratePassenger(RatingCreateRequest dto, Long passengerId) {
        DriverResponse driverResponse = getDriver(dto.getDriverId());
        RideResponse rideResponse = getRide(dto.getRideId());
        var passenger = getByIdOrElseThrow(passengerId);
        if (ratingRepository.existsByRideId(dto.getRideId())) {
            throw new BadRequestException(getAlreadyExistMessage("Rating", "rideId", dto.getRideId()));
        }

        var newRating = ratingMapper.mapToEntity(dto);
        newRating.setPassenger(passenger);
        var savedRating = ratingRepository.save(newRating);
        passenger.setRating(floorRating(getAverageRating(passengerId)));
        passengerRepository.save(passenger);
        var response = ratingMapper.mapToDto(savedRating);
        response.setRide(rideResponse);
        response.setDriver(driverResponse);
        log.info(RATE_PASSENGER_LOG, passengerId);
        return response;
    }

    @Override
    public PassengerRatingResponse getPassengerRating(Long passengerId) {
        if (passengerRepository.existsById(passengerId)) {
            log.info(GET_PASSENGER_RATING_LOG, passengerId);
            double passengerRating = getAverageRating(passengerId);
            return PassengerRatingResponse.builder()
                    .passengerId(passengerId)
                    .rating(floorRating(passengerRating))
                    .build();
        } else {
            throw new NotFoundException(getNotFoundMessage("Passenger", "passengerId", passengerId));
        }
    }

    private static double floorRating(double passengerRating) {
        return Math.floor(passengerRating * 100) / 100;
    }

    private Passenger getByIdOrElseThrow(Long passengerId) {
        return passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Passenger", "passengerId", passengerId)));
    }

    private Double getAverageRating(Long passengerId) {
        return ratingRepository.getRatingsByPassengerId(passengerId)
                .orElse(DEFAULT_RATING);
    }

    private DriverResponse getDriver(Long id) {
        return driverService.getDriver(id);
    }

    private RideResponse getRide(String id) {
        return rideService.getRide(id);
    }
}
