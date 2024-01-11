package by.sergo.driverservice.service;

import by.sergo.driverservice.domain.dto.request.RatingCreateRequestDto;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponseDto;
import by.sergo.driverservice.domain.dto.response.RatingResponseDto;
import by.sergo.driverservice.domain.entity.Rating;
import by.sergo.driverservice.repository.DriverRepository;
import by.sergo.driverservice.repository.RatingRepository;
import by.sergo.driverservice.service.exception.ExceptionMessageUtil;
import by.sergo.driverservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final DriverRepository driverRepository;
    private final RatingRepository ratingRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public RatingResponseDto createRateOfDriver(RatingCreateRequestDto dto, Long driverId) {
        var driver = driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Driver", "id", driverId)
        ));

        var rating = mapToEntity(dto);
        rating.setDriver(driver);
        var savedRating = ratingRepository.saveAndFlush(rating);
        driver.setRating(Math.floor(getAverageRating(driverId) * 100) / 100);
        driverRepository.save(driver);
        return mapToDto(savedRating);
    }

    public DriverRatingResponseDto getDriverRating(Long driverId) {
        var driver = driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Driver", "id", driverId)
        ));

        double driverRating = getAverageRating(driverId);

        return DriverRatingResponseDto.builder()
                .driverId(driverId)
                .rating(Math.floor(driverRating*100)/100)
                .build();
    }

    private double getAverageRating(Long driverId) {
        var driverRating = ratingRepository.getRatingsByDriverId(driverId)
                .stream()
                .mapToDouble(Rating::getGrade)
                .average()
                .orElse(5.0);
        return driverRating;
    }


    public RatingResponseDto mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponseDto.class);
    }

    public Rating mapToEntity(RatingCreateRequestDto dto) {
        return modelMapper.map(dto, Rating.class);
    }

}
