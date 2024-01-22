package by.sergo.rideservice;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableMongock
@EnableFeignClients
public class RideServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RideServiceApplication.class, args);
    }
}
