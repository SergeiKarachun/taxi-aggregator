package by.sergo.driverservice.controller;

import by.sergo.driverservice.domain.dto.request.DriverCreateUpdateRequest;
import by.sergo.driverservice.domain.dto.response.DriverListResponse;
import by.sergo.driverservice.domain.dto.response.DriverResponse;
import by.sergo.driverservice.service.impl.DriverServiceImpl;
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
    private final DriverServiceImpl driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DriverResponse> create(@RequestBody @Valid DriverCreateUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverResponse> update(@RequestBody @Valid DriverCreateUpdateRequest dto,
                                                 @PathVariable("id") Long id) {
        return ResponseEntity.ok(driverService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<DriverResponse> delete(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(driverService.delete(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }

    @GetMapping
    public ResponseEntity<DriverListResponse> getAll(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                     @RequestParam(value = "orderBy", required = false) String orderBy) {
        var pageListResponseDto = driverService.getAll(page, size, orderBy);
        return ResponseEntity.ok(pageListResponseDto);
    }

    @GetMapping("/available")
    public ResponseEntity<DriverListResponse> getAllAvailable(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                                                              @RequestParam(value = "orderBy", required = false) String orderBy) {
        var pageListResponseDto = driverService.getAvailableDrivers(page, size, orderBy);
        return ResponseEntity.ok(pageListResponseDto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DriverResponse> changeStatus(@PathVariable("id") Long id) {
        return ResponseEntity.ok(driverService.changeStatus(id));
    }
}
