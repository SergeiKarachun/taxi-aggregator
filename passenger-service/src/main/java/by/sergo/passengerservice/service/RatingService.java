package by.sergo.passengerservice.service;

import by.sergo.cab.passengerservice.domain.dto.PassengerRatingResponseDto;
import by.sergo.cab.passengerservice.domain.dto.RatingCreateRequestDto;
import by.sergo.cab.passengerservice.domain.dto.RatingResponseDto;
import by.sergo.cab.passengerservice.domain.entity.Rating;
import by.sergo.cab.passengerservice.repository.PassengerRepository;
import by.sergo.cab.passengerservice.repository.RatingRepository;
import by.sergo.cab.passengerservice.service.exception.ExceptionMessageUtil;
import by.sergo.cab.passengerservice.service.exception.NotFoundException;
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
        return mapToDto(ratingRepository.save(newRating));
    }

    public PassengerRatingResponseDto getPassengerRating(Long passengerId) {
        passengerRepository.findById(passengerId).orElseThrow(() -> new NotFoundException(
                ExceptionMessageUtil.getNotFoundMessage("Passenger", "passengerId", passengerId)
        ));

        var passengerRating = ratingRepository.getRatingsByPassengerId(passengerId)
                .stream()
                .mapToDouble(Rating::getGrade)
                .average()
                .orElse(5.0);
        return PassengerRatingResponseDto.builder()
                .passengerId(passengerId)
                .rating(passengerRating)
                .build();
    }

    public RatingResponseDto mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponseDto.class);
    }

    public Rating mapToEntity(RatingCreateRequestDto dto) {
        return modelMapper.map(dto, Rating.class);
    }

}
