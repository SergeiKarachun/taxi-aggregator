package by.sergo.rideservice.integration.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockPassengerService() {
        return new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockDriverService() {
        return new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    }
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockCreditCardService() {
        return new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    }
}
