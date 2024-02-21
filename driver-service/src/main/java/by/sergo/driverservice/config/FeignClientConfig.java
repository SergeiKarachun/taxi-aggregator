package by.sergo.driverservice.config;

import by.sergo.driverservice.util.ConstantUtil;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignClientConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new Custom5xxErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(ConstantUtil.RETRYER_PERIOD, ConstantUtil.RETRYER_MAX_PERIOD, ConstantUtil.RETRYER_MAX_ATTEMPTS);
    }
}
