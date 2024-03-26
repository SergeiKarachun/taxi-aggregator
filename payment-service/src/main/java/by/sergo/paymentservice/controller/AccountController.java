package by.sergo.paymentservice.controller;

import by.sergo.paymentservice.domain.dto.request.AccountCreateUpdateRequest;
import by.sergo.paymentservice.domain.dto.response.AccountResponse;
import by.sergo.paymentservice.service.impl.AccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountServiceImpl accountService;

    @PostMapping
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    @Operation(summary = "Create new account")
    public ResponseEntity<AccountResponse> create(@RequestBody @Valid AccountCreateUpdateRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete account")
    public ResponseEntity<AccountResponse> delete(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(accountService.deleteById(id));
    }

    @PutMapping("/driver/{id}")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    @Operation(summary = "Withdrawal money for driver from account to credit card")
    public ResponseEntity<AccountResponse> withdrawalBalance(@PathVariable("id") Long driverId,
                                                             @RequestParam(value = "sum", required = true) BigDecimal sum) {
        return ResponseEntity.ok(accountService.withdrawalBalance(driverId, sum));
    }

    @GetMapping("/driver/{id}")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    @Operation(summary = "Get account by driverId")
    public ResponseEntity<AccountResponse> getDriverAccount(@PathVariable("id") Long id) {
        return ResponseEntity.ok(accountService.getByDriverId(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by id")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(accountService.getById(id));
    }
}