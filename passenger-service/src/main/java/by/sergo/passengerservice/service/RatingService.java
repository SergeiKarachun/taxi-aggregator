package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequestDto;
import by.sergo.passengerservice.domain.dto.response.PassengerRatingResponseDto;
import by.sergo.passengerservice.domain.dto.response.RatingResponseDto;
import by.sergo.passengerservice.domain.entity.Rating;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.repository.RatingRepository;
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
        var newRating = mapToEntity(dto);
        newRating.setPassenger(passenger);
        var save = ratingRepository.saveAndFlush(newRating);
        var ratingResponseDto = mapToDto(save);
        passenger.setRating(getAverageRating(passengerId));
        passengerRepository.save(passenger);
        return ratingResponseDto;
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

    private double getAverageRating(Long passengerId) {
        var passengerRating = ratingRepository.getRatingsByPassengerId(passengerId)
                .stream()
                .mapToDouble(Rating::getGrade)
                .average()
                .orElse(5.0);
        return passengerRating;
    }

    public RatingResponseDto mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponseDto.class);
    }

    public Rating mapToEntity(RatingCreateRequestDto dto) {
        return modelMapper.map(dto, Rating.class);
    }
}
