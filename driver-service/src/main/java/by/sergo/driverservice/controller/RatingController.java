package by.sergo.driverservice.controller;


import by.sergo.driverservice.domain.dto.request.RatingCreateRequest;
import by.sergo.driverservice.domain.dto.response.DriverRatingResponse;
import by.sergo.driverservice.domain.dto.response.RatingResponse;
import by.sergo.driverservice.service.impl.RatingServiceImpl;
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

    private final RatingServiceImpl ratingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RatingResponse> createRatingOfDriver(@RequestBody @Valid RatingCreateRequest dto,
                                                               @PathVariable("id") Long driverId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.createRateOfDriver(dto, driverId));
    }

    @GetMapping
    public ResponseEntity<DriverRatingResponse> getDriverRating(@PathVariable("id") Long driverId) {
        return ResponseEntity.ok(ratingService.getDriverRating(driverId));
    }

}
