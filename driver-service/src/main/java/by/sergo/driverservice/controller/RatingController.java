package by.sergo.driverservice.controller;


import by.sergo.driverservice.domain.dto.request.RatingCreateRequestDto;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponseDto;
import by.sergo.driverservice.domain.dto.response.RatingResponseDto;
import by.sergo.driverservice.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers/{id}/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RatingResponseDto> createRatingOfDriver(@RequestBody @Valid RatingCreateRequestDto dto,
                                                                  @PathVariable("id") Long driverId) {
        return ResponseEntity.ok(ratingService.createRateOfDriver(dto, driverId));
    }

    @GetMapping
    public ResponseEntity<DriverRatingResponseDto> getDriverRating(@PathVariable("id") Long driverId) {
        return ResponseEntity.ok(ratingService.getDriverRating(driverId));
    }

}
