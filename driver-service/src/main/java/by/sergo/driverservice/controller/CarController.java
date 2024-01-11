package by.sergo.driverservice.controller;

import by.sergo.driverservice.domain.dto.request.CarCreateUpdateRequestDto;
import by.sergo.driverservice.domain.dto.response.CarListResponseDto;
import by.sergo.driverservice.domain.dto.response.CarResponseDto;
import by.sergo.driverservice.domain.dto.response.StringResponse;
import by.sergo.driverservice.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CarResponseDto> create(@RequestBody @Valid CarCreateUpdateRequestDto dto) {
        var carResponseDto = carService.create(dto);
        return ResponseEntity.ok(carResponseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarResponseDto> update(@RequestBody @Valid CarCreateUpdateRequestDto dto,
                                                 @PathVariable("id") Long id) {
        return ResponseEntity.ok(carService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<StringResponse> delete(@PathVariable("id") Long id) {
        carService.delete(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Car with id=" + id + " has been deleted.")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(carService.getById(id));
    }

    @GetMapping("/driver/{id}")
    public ResponseEntity<CarResponseDto> getByDriverId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(carService.getByDriverId(id));
    }

    @GetMapping
    public ResponseEntity<CarListResponseDto> getAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                     @RequestParam(value = "orderBy", required = false) String orderBy) {
        var carListResponseDto = carService.getAll(page, size, orderBy);
        return ResponseEntity.ok(carListResponseDto);
    }

}
