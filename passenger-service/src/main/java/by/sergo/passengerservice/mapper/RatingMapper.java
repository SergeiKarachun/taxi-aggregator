package by.sergo.passengerservice.mapper;

import by.sergo.passengerservice.client.DriverFeignClient;
import by.sergo.passengerservice.client.RideFeignClient;
import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.DriverResponse;
import by.sergo.passengerservice.domain.dto.response.RatingResponse;
import by.sergo.passengerservice.domain.dto.response.RideResponse;
import by.sergo.passengerservice.domain.entity.Rating;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingMapper {
    private final ModelMapper modelMapper;
    private final DriverFeignClient driverFeignClient;
    private final RideFeignClient rideFeignClient;

    public RatingResponse mapToDto(Rating rating){
        RatingResponse response = modelMapper.map(rating, RatingResponse.class);
        response.setDriver(getDriverById(rating.getDriverId()));
        response.setRide(getRideById(rating.getRideId()));
        return response;
    }

    public Rating mapToEntity(RatingCreateRequest dto) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, Rating.class);
    }

    private RideResponse getRideById(String id) {
        return rideFeignClient.getRideById(id);
    }

    private DriverResponse getDriverById(Long id) {
        return driverFeignClient.getDriverById(id);
    }
}
