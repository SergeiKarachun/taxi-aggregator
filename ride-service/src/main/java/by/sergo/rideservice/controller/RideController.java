package by.sergo.rideservice.controller;

import by.sergo.rideservice.domain.dto.request.DriverRequestDto;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequestDto;
import by.sergo.rideservice.domain.dto.response.RideListResponseDto;
import by.sergo.rideservice.domain.dto.response.RideResponseDto;
import by.sergo.rideservice.domain.dto.response.StringResponse;
import by.sergo.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Create a ride.")
    public ResponseEntity<RideResponseDto> create(@RequestBody @Valid RideCreateUpdateRequestDto dto) {
        return ResponseEntity.ok(rideService.create(dto));
    }

    @PutMapping(value = "/{id}/set-driver", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Accept a ride and assign a driver.")
    public ResponseEntity<RideResponseDto> setDriverAndAcceptRide(@RequestBody @Valid DriverRequestDto dto,
                                                     @PathVariable("id") String rideId) {
        return ResponseEntity.ok(rideService.setDriverAndAcceptRide(dto, rideId));
    }

    @PutMapping("/{id}/reject")
    @Operation(description = "Reject the ride by id.")
    public ResponseEntity<StringResponse> rejectRide(@PathVariable("id") String id) {
        rideService.rejectRide(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("You rejected ride.")
                .build());
    }

    @PutMapping("/{id}/start")
    @Operation(description = "Start a ride by id.")
    public ResponseEntity<StringResponse> startRide(@PathVariable("id") String id) {
        rideService.startRide(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("The ride has begun.")
                .build());
    }

    @PutMapping("/{id}/end")
    @Operation(description = "End a ride by id")
    public ResponseEntity<StringResponse> endRide(@PathVariable("id") String id) {
        rideService.endRide(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("The ride finished.")
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete a ride by id.")
    public ResponseEntity<StringResponse> deleteById(@PathVariable("id") String id) {
        rideService.deleteById(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Ride with id=" + id + " has been deleted.")
                .build());
    }

    @GetMapping("/{id}")
    @Operation(description = "Get a ride by id.")
    public ResponseEntity<RideResponseDto> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(rideService.getById(id));
    }

    @GetMapping("/passenger/{id}")
    @Operation(description = "Get passenger rides.")
    public ResponseEntity<RideListResponseDto> getByPassengerId(@PathVariable("id") Long id,
                                                                @RequestParam(value = "status", required = true) String status,
                                                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                                @RequestParam(value = "orderBy", required = false) String orderBy) {
        var rideListResponseDto = rideService.getByPassengerId(id, status, page, size, orderBy);
        return ResponseEntity.ok(rideListResponseDto);
    }

    @GetMapping("/driver/{id}")
    @Operation(description = "Get driver rides.")
    public ResponseEntity<RideListResponseDto> getByDriverId(@PathVariable("id") Long id,
                                                             @RequestParam(value = "status", required = true) String status,
                                                             @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                             @RequestParam(value = "orderBy", required = false) String orderBy) {
        var rideListResponseDto = rideService.getByDriverId(id, status, page, size, orderBy);
        return ResponseEntity.ok(rideListResponseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update a ride by id.")
    public ResponseEntity<RideResponseDto> update(@RequestBody @Valid RideCreateUpdateRequestDto dto,
                                                  @PathVariable("id") String id) {
        var rideResponseDto = rideService.update(dto, id);
        return ResponseEntity.ok(rideResponseDto);
    }
}
