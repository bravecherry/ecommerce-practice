package fastcampus.ecommerce.batch.jobconfig.product.download;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import fastcampus.ecommerce.batch.BaseBatchIntegrationTest;
import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.service.product.ProductService;
import fastcampus.ecommerce.batch.util.DateTimeUtils;
import fastcampus.ecommerce.batch.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
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

@TestPropertySource(properties = {"spring.batch.job.name=productDownloadJob"})
class ProductDownloadJobConfigurationTest extends BaseBatchIntegrationTest {

    @Value("classpath:/data/products_downloaded_expected.csv")
    private Resource expectedResource;

    File outputFile;

    @Autowired
    private ProductService productService;

    @Test
    void testJob(@Autowired Job productDownloadJob) throws Exception {
        // 다운로드 전에 상품이 저장되어 있어야 하기 떄문에 호출
        saveProducts();

        // 저장된 상품들을 파일에 써야 하므로 설정
        // 임시파일로 지정
        outputFile = FileUtils.createTmpFile("products_downloaded", ".csv");
        JobParameters jobParameters = getJobParameters();
        jobLauncherTestUtils.setJob(productDownloadJob);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertAll(
            () -> assertThat(Files.readString(Path.of(outputFile.getPath()))).isEqualTo(
                Files.readString(Path.of(expectedResource.getFile().getPath()))),
            () -> assertJobCompleted(jobExecution)
        );
    }

    private void saveProducts() {
        productService.save(Product.of(
            "1", 56L, "음식/요리", "스니커즈", LocalDate.of(2022, 8, 16), LocalDate.of(2023, 7, 8),
            "OUT_OF_STOCK", "현대",
            "삼성전자", 477726, 706, DateTimeUtils.toLocalDateTime("2026-01-15 00:00:00.000"),
            DateTimeUtils.toLocalDateTime("2026-01-15 00:00:00.000")
        ));
        productService.save(Product.of(
            "2", 5L, "IT/기술", "수송기계", LocalDate.of(2022, 9, 14), LocalDate.of(2025, 9, 20),
            "DISCONTINUED", "네이버", "삼성SDI", 56264, 950,
            DateTimeUtils.toLocalDateTime("2026-01-15 00:00:00.000"),
            DateTimeUtils.toLocalDateTime("2026-01-15 00:00:00.000")
        ));
    }

    private JobParameters getJobParameters() throws IOException {
        return new JobParametersBuilder()
            .addJobParameter("outputFilePath",
                new JobParameter<>(outputFile.getPath(), String.class, false))
            .addJobParameter("gridSize",
                new JobParameter<>(2, Integer.class, false))
            .toJobParameters();
    }

}