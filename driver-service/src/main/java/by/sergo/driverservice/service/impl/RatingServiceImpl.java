package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.mapper.RatingMapper;
import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.repository.RatingRepository;
import by.sergo.driverservice.service.PassengerService;
import by.sergo.driverservice.service.RatingService;
import by.sergo.driverservice.service.RideService;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static by.sergo.driverservice.util.ConstantUtil.DEFAULT_RATING;
import static by.sergo.driverservice.util.ExceptionMessageUtil.getAlreadyExistMessage;
import static by.sergo.driverservice.util.ExceptionMessageUtil.getNotFoundMessage;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

    private final DriverRepository driverRepository;
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final PassengerService passengerService;
    private final RideService rideService;

    @Override
    @Transactional
    public RatingResponse createRateOfDriver(RatingCreateRequest dto, Long driverId) {
        PassengerResponse passengerResponse = getPassenger(dto.getPassengerId());
        RideResponse rideResponse = getRide(dto.getRideId());
        var driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException(getNotFoundMessage("Driver", "id", driverId)));

        if (ratingRepository.existsByRideId(dto.getRideId())) {
            throw new BadRequestException(getAlreadyExistMessage("Rating", "rideId", dto.getRideId()));
        }

        var rating = ratingMapper.mapToEntity(dto);
        rating.setDriver(driver);
        var savedRating = ratingRepository.save(rating);
        driver.setRating(getFloorRating(getAverageRating(driverId)));
        driverRepository.save(driver);
        var response = ratingMapper.mapToDto(savedRating);
        response.setPassenger(passengerResponse);
        response.setRide(rideResponse);
        return response;
    }

    @Override
    public DriverRatingResponse getDriverRating(Long driverId) {
        if (driverRepository.existsById(driverId)) {
            double driverRating = getAverageRating(driverId);
            return DriverRatingResponse.builder()
                    .driverId(driverId)
                    .rating(getFloorRating(driverRating))
                    .build();
        } else throw new NotFoundException(getNotFoundMessage("Driver", "id", driverId));
    }

    private Double getAverageRating(Long driverId) {
        return ratingRepository.getRatingsByDriverId(driverId).orElse(DEFAULT_RATING);
    }

    private double getFloorRating(Double driverId) {
        return Math.floor(driverId * 100) / 100;
    }

    private PassengerResponse getPassenger(Long id) {
        return passengerService.getPassenger(id);
    }

    private RideResponse getRide(String id) {
        return rideService.getRide(id);
    }
}
