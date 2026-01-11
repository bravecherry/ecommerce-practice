package fastcampus.ecommerce.batch;

import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Bean
    public PushGateway pushGateway() {
        return PushGateway.builder()
                .address("localhost:9091")
                .job("spring-batch")
                .groupingKey("env", "local")
                .build();
    }

}