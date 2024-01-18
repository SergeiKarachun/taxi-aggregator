package by.sergo.passengerservice.mapper;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;
import by.sergo.passengerservice.domain.entity.Passenger;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerMapper {
    private final ModelMapper modelMapper;

    public Passenger mapToEntity(PassengerCreateUpdateRequest passengerResponseDto) {
        return modelMapper.map(passengerResponseDto, Passenger.class);
    }

    public PassengerResponse mapToDto(Passenger passenger) {
        return modelMapper.map(passenger, PassengerResponse.class);
    }
}
