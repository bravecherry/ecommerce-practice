package fastcampus.ecommerce.batch;

import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
                .groupingKey("job_name", "batch_job")
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 기본으로 운영할 풀 사이즈(최소 운영 사이즈)
        executor.setCorePoolSize(128);
        // 생성 가능한 최대 풀 사이즈
        executor.setMaxPoolSize(128);
        // 작업 queue의 최대 용량
        executor.setQueueCapacity(128);
        //코어 스레드도 지정된 시간 동안 유휴 상태일 경우 타임아웃되어 스레드 풀에서 제거됨
        // 이를 통해 스레드 풀은 활동이 적은 기간 동안 크기를 동적으로 줄여 0으로 축소할 수 있으므로 시스템 리소스(메모리)를 절약할 수 있다
        executor.setAllowCoreThreadTimeOut(true);
        // executor 서비스가 종료하도록 명령을 받았을 때 하위에 실행되고 있는 스레드들이 태스크를 전부 종료하고 나서 executor 를 종료한다는 의미
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 종료 명령을 받고 기다리는 시간
        executor.setAwaitTerminationSeconds(10);
        return executor;
    }

}