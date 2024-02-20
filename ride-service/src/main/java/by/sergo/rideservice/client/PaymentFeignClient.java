package by.sergo.rideservice.client;

import by.sergo.rideservice.config.FeignClientConfig;
import by.sergo.rideservice.domain.dto.response.CreditCardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.payment.name}",
        configuration = FeignClientConfig.class,
        path = "${feign.client.config.payment.path}")
public interface PaymentFeignClient {
    @GetMapping("/passenger/{id}")
    CreditCardResponse getPassengerCreditCard(@PathVariable("id") Long id);
}
