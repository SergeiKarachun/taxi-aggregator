package by.sergo.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/driver-service")
    public String driverServiceFallback() {
        return "Driver-service is not available, please try later.";
    }

    @GetMapping("/passenger-service")
    public String passengerServiceFallback() {
        return "Passenger-service is not available, please try later.";
    }

    @GetMapping("/ride-service")
    public String rideServiceFallback() {
        return "Ride-service is not available, please try later.";
    }

    @GetMapping("/payment-service")
    public String paymentServiceFallback() {
        return "Payment-service is not available, please try later.";
    }
}
