package by.sergo.passengerservice.service.impl;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerRatingResponse;
import by.sergo.passengerservice.domain.dto.response.RatingResponse;
import by.sergo.passengerservice.domain.entity.Passenger;
import by.sergo.passengerservice.domain.entity.Rating;
import by.sergo.passengerservice.repository.PassengerRepository;
import by.sergo.passengerservice.repository.RatingRepository;
import by.sergo.passengerservice.service.RatingService;
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
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RatingResponse createRateOfPassenger(RatingCreateRequest dto, Long passengerId) {
        var passenger = getByIdOrElseThrow(passengerId);
        if (ratingRepository.existsByRideId(dto.getRideId())) {
            throw new BadRequestException(ExceptionMessageUtil.getAlreadyExistMessage("Rating", "rideId", dto.getRideId().toString()));
        }

        var newRating = mapToEntity(dto);
        newRating.setPassenger(passenger);
        var savedRating = ratingRepository.save(newRating);
        passenger.setRating(floorRating(getAverageRating(passengerId)));
        passengerRepository.save(passenger);
        return mapToDto(savedRating);
    }

    @Override
    public PassengerRatingResponse getPassengerRating(Long passengerId) {
        if (passengerRepository.existsById(passengerId)){
            double passengerRating = getAverageRating(passengerId);
            return PassengerRatingResponse.builder()
                    .passengerId(passengerId)
                    .rating(floorRating(passengerRating))
                    .build();
        }
        else throw new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Passenger", "passengerId", passengerId));
    }

    private static double floorRating(double passengerRating) {
        return Math.floor(passengerRating * 100) / 100;
    }

    private Passenger getByIdOrElseThrow(Long passengerId) {
        return passengerRepository.findById(passengerId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessageUtil.getNotFoundMessage("Passenger", "passengerId", passengerId)));
    }

    private Double getAverageRating(Long passengerId) {
        return ratingRepository.getRatingsByPassengerId(passengerId).orElse(5.0);
    }

    public RatingResponse mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponse.class);
    }

    public Rating mapToEntity(RatingCreateRequest dto) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, Rating.class);
    }
}
