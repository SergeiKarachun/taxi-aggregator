package by.sergo.rideservice.controller;

import by.sergo.rideservice.domain.dto.request.DriverRequest;
import by.sergo.rideservice.domain.dto.request.RideCreateUpdateRequest;
import by.sergo.rideservice.domain.dto.response.RideListResponse;
import by.sergo.rideservice.domain.dto.response.RideResponse;
import by.sergo.rideservice.service.impl.RideServiceImpl;
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

    private final RideServiceImpl rideService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Create a ride.")
    public ResponseEntity<RideResponse> create(@RequestBody @Valid RideCreateUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.create(dto));
    }

    @PutMapping(value = "/{id}/set-driver", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Accept a ride and assign a driver.")
    public ResponseEntity<RideResponse> setDriverAndAcceptRide(@RequestBody @Valid DriverRequest dto,
                                                               @PathVariable("id") String rideId) {
        return ResponseEntity.ok(rideService.setDriverAndAcceptRide(dto, rideId));
    }

    @PutMapping("/{id}/reject")
    @Operation(description = "Reject the ride by id.")
    public ResponseEntity<RideResponse> rejectRide(@PathVariable("id") String id) {
        return ResponseEntity.ok(rideService.rejectRide(id));
    }

    @PutMapping("/{id}/start")
    @Operation(description = "Start a ride by id.")
    public ResponseEntity<RideResponse> startRide(@PathVariable("id") String id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PutMapping("/{id}/end")
    @Operation(description = "End a ride by id")
    public ResponseEntity<RideResponse> endRide(@PathVariable("id") String id) {
        return ResponseEntity.ok(rideService.endRide(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete a ride by id.")
    public ResponseEntity<RideResponse> deleteById(@PathVariable("id") String id) {

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rideService.deleteById(id));
    }

    @GetMapping("/{id}")
    @Operation(description = "Get a ride by id.")
    public ResponseEntity<RideResponse> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(rideService.getById(id));
    }

    @GetMapping("/passenger/{id}")
    @Operation(description = "Get passenger rides.")
    public ResponseEntity<RideListResponse> getByPassengerId(@PathVariable("id") Long id,
                                                             @RequestParam(value = "status", required = true) String status,
                                                             @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                             @RequestParam(value = "orderBy", required = false) String orderBy) {
        var rideListResponseDto = rideService.getByPassengerId(id, status, page, size, orderBy);
        return ResponseEntity.ok(rideListResponseDto);
    }

    @GetMapping("/driver/{id}")
    @Operation(description = "Get driver rides.")
    public ResponseEntity<RideListResponse> getByDriverId(@PathVariable("id") Long id,
                                                          @RequestParam(value = "status", required = true) String status,
                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                          @RequestParam(value = "orderBy", required = false) String orderBy) {
        var rideListResponseDto = rideService.getByDriverId(id, status, page, size, orderBy);
        return ResponseEntity.ok(rideListResponseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update a ride by id.")
    public ResponseEntity<RideResponse> update(@RequestBody @Valid RideCreateUpdateRequest dto,
                                               @PathVariable("id") String id) {
        return ResponseEntity.ok(rideService.update(dto, id));
    }
}
