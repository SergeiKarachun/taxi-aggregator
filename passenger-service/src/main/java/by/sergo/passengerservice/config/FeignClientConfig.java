package by.sergo.passengerservice.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static by.sergo.passengerservice.util.ConstantUtil.*;

@Configuration
public class FeignClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new Custom5xxErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(RETRYER_PERIOD, RETRYER_MAX_PERIOD, RETRYER_MAX_ATTEMPTS);
    }
}
