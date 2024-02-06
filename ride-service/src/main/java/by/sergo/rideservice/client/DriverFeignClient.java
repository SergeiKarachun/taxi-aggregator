package by.sergo.rideservice.client;


import by.sergo.rideservice.config.FeignClientConfig;
import by.sergo.rideservice.domain.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.driver.name}", url = "${feign.client.config.driver.url}",
        configuration = FeignClientConfig.class, path = "${feign.client.config.driver.path}")
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriverById(@PathVariable("id") Long id);
}
