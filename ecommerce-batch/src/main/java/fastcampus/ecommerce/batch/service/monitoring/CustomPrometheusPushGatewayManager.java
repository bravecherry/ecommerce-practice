package fastcampus.ecommerce.batch.service.monitoring;

import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusPushGatewayManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomPrometheusPushGatewayManager extends PrometheusPushGatewayManager {

    private final PushGateway pushGateway;

    public CustomPrometheusPushGatewayManager(PushGateway pushGateway) {
        super(pushGateway, Duration.ofSeconds(30), ShutdownOperation.POST);
        this.pushGateway = pushGateway;
    }

    public void pushMetric() {
        try {
            this.pushGateway.pushAdd();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
