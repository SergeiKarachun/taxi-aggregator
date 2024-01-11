package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequestDto;
import by.sergo.passengerservice.domain.dto.response.PassengerRatingResponseDto;
import by.sergo.passengerservice.domain.dto.response.RatingResponseDto;
import by.sergo.passengerservice.domain.entity.Rating;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.repository.RatingRepository;
import by.sergo.passengerservice.service.exception.BadRequestException;
import by.sergo.passengerservice.service.exception.ExceptionMessageUtil;
import by.sergo.passengerservice.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public RatingResponseDto createRateOfPassenger(RatingCreateRequestDto dto, Long passengerId) {
        var passenger = passengerRepository.findById(passengerId).orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Passenger", "passengerId", passengerId)
        ));
        if (ratingRepository.existsByRideId(dto.getRideId())) {
            throw new BadRequestException(
                    ExceptionMessageUtil.getAlreadyExistMessage("Rating", "rideId", dto.getRideId().toString()));
        }

        var newRating = mapToEntity(dto);
        newRating.setPassenger(passenger);
        var savedRating = ratingRepository.saveAndFlush(newRating);
        passenger.setRating(Math.floor(getAverageRating(passengerId) * 100) / 100);
        passengerRepository.save(passenger);
        return mapToDto(savedRating);
    }

    public PassengerRatingResponseDto getPassengerRating(Long passengerId) {
        passengerRepository.findById(passengerId).orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Passenger", "passengerId", passengerId)
        ));

        double passengerRating = getAverageRating(passengerId);
        return PassengerRatingResponseDto.builder()
                .passengerId(passengerId)
                .rating(Math.floor(passengerRating*100)/100)
                .build();
    }

    private Double getAverageRating(Long passengerId) {
        return ratingRepository.getRatingsByPassengerId(passengerId).orElse(5.0);
    }

    public RatingResponseDto mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponseDto.class);
    }

    public Rating mapToEntity(RatingCreateRequestDto dto) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, Rating.class);
    }
}
