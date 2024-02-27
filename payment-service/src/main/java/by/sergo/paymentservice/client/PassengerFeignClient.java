package by.sergo.paymentservice.client;

import by.sergo.paymentservice.config.FeignClientConfig;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.passenger.name}",
        configuration = FeignClientConfig.class,
        path = "${feign.client.config.passenger.path}")
public interface PassengerFeignClient {
    @GetMapping("/{id}")
    UserResponse getPassengerById(@PathVariable("id") Long id);
}
