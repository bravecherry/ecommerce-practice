package fastcampus.ecommerce.batch.jobconfig.transaction.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import fastcampus.ecommerce.batch.BaseBatchIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.batch.job.name=transactionReportJob"})
class TransactionReportJobConfigurationTest extends BaseBatchIntegrationTest {

    @Value("classpath:/logs/transaction.log")
    private Resource resource;

    @Test
    void testJob(@Autowired Job transactionReportJob) throws Exception {
        jobLauncherTestUtils.setJob(transactionReportJob);
        JobParameters jobParameters = new JobParametersBuilder()
            .addJobParameter("inputFilePath",
                new JobParameter<>(resource.getFile().getPath(), String.class, false))
            .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertAll(
            () -> assertThat(jdbcTemplate.queryForObject(
                "select count(*) from transaction_reports", Integer.class))
                .isEqualTo(3),
            () -> assertJobCompleted(jobExecution)
        );
    }
}