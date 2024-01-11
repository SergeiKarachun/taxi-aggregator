package by.sergo.driverservice.controller;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequestDto;
import by.sergo.driverservice.domain.dto.response.DriverListResponseDto;
import by.sergo.driverservice.domain.dto.response.DriverResponseDto;
import by.sergo.driverservice.domain.dto.response.StringResponse;
import by.sergo.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DriverResponseDto> create(@RequestBody @Valid DriverCreateUpdateRequestDto dto) {
        var driverResponseDto = driverService.create(dto);
        return ResponseEntity.ok(driverResponseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverResponseDto> update(@RequestBody @Valid DriverCreateUpdateRequestDto dto,
                                                    @PathVariable("id") Long id) {
        return ResponseEntity.ok(driverService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<StringResponse> delete(@PathVariable("id") Long id) {
        driverService.delete(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Driver with id" + id + " has been deleted")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }

    @GetMapping
    public ResponseEntity<DriverListResponseDto> getAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                        @RequestParam(value = "orderBy", required = false) String orderBy) {
        var pageListResponseDto = driverService.getAll(page, size, orderBy);
        return ResponseEntity.ok(pageListResponseDto);
    }

    @GetMapping("/available")
    public ResponseEntity<DriverListResponseDto> getAllAvailable(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                        @RequestParam(value = "orderBy", required = false) String orderBy) {
        var pageListResponseDto = driverService.getAvailableDrivers(page, size, orderBy);
        return ResponseEntity.ok(pageListResponseDto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<StringResponse> changeStatus(@PathVariable("id") Long id) {
        driverService.changeStatus(id);
        return ResponseEntity.ok(StringResponse.builder()
                .message("Status successfully has been changed!")
                .build());
    }



}
