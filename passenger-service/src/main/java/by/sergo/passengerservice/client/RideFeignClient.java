package by.sergo.passengerservice.client;

import by.sergo.passengerservice.config.FeignClientConfig;
import by.sergo.passengerservice.domain.dto.response.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.ride.name}",
        configuration = FeignClientConfig.class,
        path = "${feign.client.config.ride.path}")
public interface RideFeignClient {
    @GetMapping("/{id}")
    RideResponse getRideById(@PathVariable("id") String id);
}
