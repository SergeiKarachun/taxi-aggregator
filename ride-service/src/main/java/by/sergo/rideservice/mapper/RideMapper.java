package by.sergo.rideservice.mapper;

import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideMapper {
    private final ModelMapper modelMapper;

    public RideResponse mapToDto(Ride ride) {
        return modelMapper.map(ride, RideResponse.class);
    }

    public Ride mapToEntity(RideCreateUpdateRequest dto) {
        return modelMapper.map(dto, Ride.class);
    }
}
