package by.sergo.paymentservice.controller;

import by.sergo.paymentservice.domain.dto.request.CreditCardCreateUpdate;
import by.sergo.paymentservice.domain.dto.request.PaymentRequest;
import by.sergo.paymentservice.domain.dto.response.CreditCardResponse;
import by.sergo.paymentservice.service.impl.CreditCardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/creditcards")
@RequiredArgsConstructor
public class CreditCardController {
    private final CreditCardServiceImpl creditCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add credit card")
    public ResponseEntity<CreditCardResponse> addCard(@RequestBody @Valid CreditCardCreateUpdate dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardService.addCard(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Change credit card")
    public ResponseEntity<CreditCardResponse> changeCard(@PathVariable("id") Long id,
                                                         @RequestBody @Valid CreditCardCreateUpdate dto) {
        return ResponseEntity.ok(creditCardService.changeCard(dto, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete credit card")
    public ResponseEntity<CreditCardResponse> deleteBard(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(creditCardService.deleteById(id));
    }

    @GetMapping("/passenger/{id}")
    @Operation(summary = "Get credit card by passengerId")
    public ResponseEntity<CreditCardResponse> getPassengerCard(@PathVariable("id") Long passengerId) {
        return ResponseEntity.ok(creditCardService.getPassengerCard(passengerId));
    }

    @GetMapping("/driver/{id}")
    @Operation(summary = "Get credit card by driverId")
    public ResponseEntity<CreditCardResponse> getDriverCard(@PathVariable("id") Long driverId) {
        return ResponseEntity.ok(creditCardService.getDriverCard(driverId));
    }

    @PostMapping("/payment")
    @Operation(summary = "Passenger payment for the trip")
    public ResponseEntity<CreditCardResponse> makePayment(@RequestBody @Valid PaymentRequest dto) {
        return ResponseEntity.ok(creditCardService.makePayment(dto));
    }
}
