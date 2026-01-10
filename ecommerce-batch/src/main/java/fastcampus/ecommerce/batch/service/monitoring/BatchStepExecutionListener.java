package fastcampus.ecommerce.batch.service.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
//step 실행 시작/종료 확인, 로그에 남는다.
public class BatchStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("before step");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("after step - execution context {}", stepExecution.getExecutionContext());
        return ExitStatus.COMPLETED;
    }
}
