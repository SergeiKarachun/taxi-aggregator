package by.sergo.driverservice.service.impl;

import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.entity.Rating;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.repository.RatingRepository;
import by.sergo.driverservice.service.RatingService;
import by.sergo.driverservice.service.exception.BadRequestException;
import by.sergo.driverservice.service.exception.ExceptionMessageUtil;
import by.sergo.driverservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

    private final DriverRepository driverRepository;
    private final RatingRepository ratingRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RatingResponse createRateOfDriver(RatingCreateRequest dto, Long driverId) {
        var driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Driver", "id", driverId)));

        if (ratingRepository.existsByRideId(dto.getRideId())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Rating", "rideId", dto.getRideId().toString()));
        }

        var rating = mapToEntity(dto);
        rating.setDriver(driver);
        var savedRating = ratingRepository.saveAndFlush(rating);
        driver.setRating(getFloorRating(getAverageRating(driverId)));
        driverRepository.save(driver);
        return mapToDto(savedRating);
    }

    @Override
    public DriverRatingResponse getDriverRating(Long driverId) {
        if (driverRepository.existsById(driverId)) {
            double driverRating = getAverageRating(driverId);
            return DriverRatingResponse.builder()
                    .driverId(driverId)
                    .rating(getFloorRating(driverRating))
                    .build();
        } else throw new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Driver", "id", driverId));
    }

    private Double getAverageRating(Long driverId) {
        return ratingRepository.getRatingsByDriverId(driverId).orElse(5.0);
    }

    private double getFloorRating(Double driverId) {
        return Math.floor(driverId * 100) / 100;
    }

    public RatingResponse mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponse.class);
    }

    public Rating mapToEntity(RatingCreateRequest dto) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, Rating.class);
    }
}
