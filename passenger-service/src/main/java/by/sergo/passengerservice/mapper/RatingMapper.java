package by.sergo.passengerservice.mapper;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.RatingResponse;
import by.sergo.passengerservice.domain.entity.Rating;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingMapper {
    private final ModelMapper modelMapper;

    public RatingResponse mapToDto(Rating rating){
        return modelMapper.map(rating, RatingResponse.class);
    }

    public Rating mapToEntity(RatingCreateRequest dto) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper.map(dto, Rating.class);
    }
}
