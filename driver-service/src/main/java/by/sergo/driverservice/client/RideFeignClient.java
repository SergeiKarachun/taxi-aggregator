package by.sergo.driverservice.client;

import by.sergo.driverservice.config.FeignClientConfig;
import by.sergo.driverservice.domain.dto.response.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.ride.name}", url = "${feign.client.config.ride.url}",
        configuration = FeignClientConfig.class, path = "${feign.client.config.ride.path}")
public interface RideFeignClient {
    @GetMapping("/{id}")
    RideResponse getRideById(@PathVariable("id") String id);
}
