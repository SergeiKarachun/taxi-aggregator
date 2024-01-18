package by.sergo.paymentservice.controller;

import by.sergo.paymentservice.domain.dto.response.ListTransactionStoreResponse;
import by.sergo.paymentservice.service.impl.TransactionStoreServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionStoreController {
    private final TransactionStoreServiceImpl transactionStoreService;

    @GetMapping("/driver/{id}")
    @Operation(summary = "Get all transaction by driverId")
    public ResponseEntity<ListTransactionStoreResponse> getDriverTransaction(@PathVariable("id") Long driverId,
                                                                             @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                             @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(transactionStoreService.getDriverTransactionByDriverId(driverId, page, size));
    }

    @GetMapping("/passenger/{id}")
    @Operation(summary = "Get all transaction by passengerId")
    public ResponseEntity<ListTransactionStoreResponse> getPassengerTransaction(@PathVariable("id") Long passengerId,
                                                                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                                @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(transactionStoreService.getPassengerTransactionByPassengerId(passengerId, page, size));
    }

}
