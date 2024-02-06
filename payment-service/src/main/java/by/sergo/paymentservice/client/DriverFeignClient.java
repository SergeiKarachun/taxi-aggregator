package by.sergo.paymentservice.client;

import by.sergo.paymentservice.config.FeignClientConfig;
import by.sergo.paymentservice.domain.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.client.config.driver.name}", url = "${feign.client.config.driver.url}",
        configuration = FeignClientConfig.class, path = "${feign.client.config.driver.path}")
public interface DriverFeignClient {
    @GetMapping("/{id}")
    UserResponse getDriverById(@PathVariable("id") Long id);
}
