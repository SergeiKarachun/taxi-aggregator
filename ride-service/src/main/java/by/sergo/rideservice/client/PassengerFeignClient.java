package by.sergo.rideservice.client;


import by.sergo.rideservice.config.FeignClientConfig;
import by.sergo.rideservice.domain.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.passenger.name}", url = "${feign.client.config.passenger.url}",
        configuration = FeignClientConfig.class, path = "${feign.client.config.passenger.path}")
public interface PassengerFeignClient {
    @GetMapping("/{id}")
    PassengerResponse getPassengerById(@PathVariable("id") Long id);
}
