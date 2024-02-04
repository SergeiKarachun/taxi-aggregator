package by.sergo.rideservice.mapper;

import by.sergo.rideservice.client.DriverFeignClient;
import by.sergo.rideservice.client.PassengerFeignClient;
import by.sergo.rideservice.domain.Ride;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.DriverResponse;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideMapper {
    private final ModelMapper modelMapper;
    private final DriverFeignClient driverFeignClient;
    private final PassengerFeignClient passengerFeignClient;

    public RideResponse mapToDto(Ride ride) {
        RideResponse response = modelMapper.map(ride, RideResponse.class);
        response.setPassenger(getPassengerById(ride.getPassengerId()));
        if (ride.getDriverId() != null) {
            response.setDriver(getDriverById(ride.getDriverId()));
        } else {
            response.setDriver(null);
        }
        return response;
    }

    public Ride mapToEntity(RideCreateUpdateRequest dto) {
        return modelMapper.map(dto, Ride.class);
    }

    private PassengerResponse getPassengerById(Long id) {
        return passengerFeignClient.getPassengerById(id);
    }

    private DriverResponse getDriverById(Long id) {
        return driverFeignClient.getDriverById(id);
    }
}
