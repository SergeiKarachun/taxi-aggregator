package by.sergo.paymentservice.integration.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockPassengerService() {
        return new WireMockServer(9001);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockDriverService() {
        return new WireMockServer(9002);
    }
}
