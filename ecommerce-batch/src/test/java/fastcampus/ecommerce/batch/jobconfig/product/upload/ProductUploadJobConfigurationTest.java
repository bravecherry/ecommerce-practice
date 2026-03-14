package fastcampus.ecommerce.batch.jobconfig.product.upload;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fastcampus.ecommerce.batch.BaseBatchIntegrationTest;
import fastcampus.ecommerce.batch.service.product.ProductService;
import java.io.IOException;
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

// job이 여러 개일 경우에는 Job 이름을 명시해줘야 한다.
@TestPropertySource(properties = {"spring.batch.job.name=productUploadJob"})
class ProductUploadJobConfigurationTest extends BaseBatchIntegrationTest {

    @Value("classpath:/data/products_for_upload.csv")
    private Resource input;

    @Autowired
    protected ProductService productService;

    @Test
    void testJob(@Autowired Job productUploadJob) throws Exception {
        JobParameters jobParameters = getJobParameters();
        jobLauncherTestUtils.setJob(productUploadJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Long count = productService.countProducts();

        assertAll(
            () -> assertEquals(count, 9L),
            () -> assertJobCompleted(jobExecution)
        );
    }

    private JobParameters getJobParameters() throws IOException {
        return new JobParametersBuilder()
            .addJobParameter("inputFilePath", new JobParameter<>(
                input.getFile().getPath(), String.class, false))
            .addJobParameter("gridSize", new JobParameter<>(3, Integer.class, false))
            .toJobParameters();
    }
}