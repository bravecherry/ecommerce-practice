package fastcampus.ecommerce.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@AutoConfigureObservability
@SpringBatchTest
@SpringJUnitConfig(classes = {BatchApplication.class})
public abstract class BaseBatchIntegrationTest {

    // job launcher 실행 환경 제공
    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    protected static void assertJobCompleted(JobExecution jobExecution) {
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }

}
