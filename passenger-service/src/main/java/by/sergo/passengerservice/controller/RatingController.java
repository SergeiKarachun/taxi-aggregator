package by.sergo.passengerservice.controller;

import by.sergo.passengerservice.domain.dto.request.RatingCreateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerRatingResponse;
import by.sergo.passengerservice.domain.dto.response.RatingResponse;
import by.sergo.passengerservice.service.impl.RatingServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers/{id}/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingServiceImpl ratingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatingResponse> createRateOfPassenger(@RequestBody @Valid RatingCreateRequest dto,
                                                                @PathVariable("id") Long passengerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(ratingService.createRateOfPassenger(dto, passengerId));
    }

    @GetMapping
    public ResponseEntity<PassengerRatingResponse> getPassengerRating(@PathVariable("id") Long passengerId) {
        return ResponseEntity.ok(ratingService.getPassengerRating(passengerId));
    }
}
