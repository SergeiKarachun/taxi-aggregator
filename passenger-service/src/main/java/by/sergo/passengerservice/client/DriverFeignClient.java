package by.sergo.passengerservice.client;

import by.sergo.passengerservice.config.FeignClientConfig;
import by.sergo.passengerservice.domain.dto.response.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.driver.name}",
        configuration = FeignClientConfig.class,
        path = "${feign.client.config.driver.path}")
public interface DriverFeignClient {
    @GetMapping("/{id}")
    DriverResponse getDriverById(@PathVariable("id") Long id);
}
