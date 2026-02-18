package fastcampus.ecommerce.batch.service.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
//job 실행 시작/종료 확인, 로그에 남는다.
public class BatchJobExecutionListener implements JobExecutionListener {

    private final CustomPrometheusPushGatewayManager manager;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("listener: beforeJob");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("listener: afterJob {}", jobExecution.getExecutionContext());

        manager.pushMetric();
    }

}
