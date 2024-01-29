package by.sergo.driverservice.mapper;

import by.sergo.driverservice.client.PassengerFeignClient;
import by.sergo.driverservice.client.RideFeignClient;
import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.PassengerResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import by.sergo.driverservice.domain.entity.Rating;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingMapper {
    private final ModelMapper modelMapper;
    private final PassengerFeignClient passengerFeignClient;
    private final RideFeignClient rideFeignClient;

    public RatingResponse mapToDto(Rating rating){
        RatingResponse response = modelMapper.map(rating, RatingResponse.class);
        response.setPassenger(getPassengerById(rating.getPassengerId()));
        response.setRide(getRideById(rating.getRideId()));
        return response;

    }

    public Rating mapToEntity(RatingCreateRequest dto) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, Rating.class);
    }

    private PassengerResponse getPassengerById(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }

    private RideResponse getRideById(String id) {
        return rideFeignClient.getRideById(id);
    }

}
