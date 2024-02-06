package by.sergo.driverservice.mapper;

import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.domain.entity.Rating;
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
