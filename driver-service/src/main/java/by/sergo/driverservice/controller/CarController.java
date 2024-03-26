package by.sergo.driverservice.controller;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.CarListResponse;
import by.sergo.driverservice.domain.dto.response.CarResponse;
import by.sergo.driverservice.service.impl.CarServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarServiceImpl carService;

    @PostMapping
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    public ResponseEntity<CarResponse> create(@RequestBody @Valid CarCreateUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(carService.create(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    public ResponseEntity<CarResponse> update(@RequestBody @Valid CarCreateUpdateRequest dto,
                                              @PathVariable("id") Long id) {
        return ResponseEntity.ok(carService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarResponse> delete(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .body(carService.delete(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    public ResponseEntity<CarResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(carService.getById(id));
    }

    @GetMapping("/driver/{id}")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    public ResponseEntity<CarResponse> getByDriverId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(carService.getByDriverId(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarListResponse> getAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                  @RequestParam(value = "orderBy", required = false) String orderBy) {
        var carListResponseDto = carService.getAll(page, size, orderBy);
        return ResponseEntity.ok(carListResponseDto);
    }

}
