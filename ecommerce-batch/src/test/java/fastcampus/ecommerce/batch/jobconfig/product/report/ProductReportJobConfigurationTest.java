package fastcampus.ecommerce.batch.jobconfig.product.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import fastcampus.ecommerce.batch.BaseBatchIntegrationTest;
import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.service.product.ProductService;
import fastcampus.ecommerce.batch.service.product.report.ProductReportService;
import fastcampus.ecommerce.batch.util.DateTimeUtils;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.batch.job.name=productReportJob"})
class ProductReportJobConfigurationTest extends BaseBatchIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductReportService productReportService;

    @Test
    public void testJob(@Autowired Job productReportJob) {
        LocalDate now = LocalDate.now();
        saveProducts();
        jobLauncherTestUtils.setJob(productReportJob);
        JobParameters jobParameters = new JobParametersBuilder()
            .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(productReportJob);

        assertAll(
            () -> assertThat(productReportService.countCategoryReports(now)).isEqualTo(1),
            () -> assertThat(productReportService.countBrandReports(now)).isEqualTo(2),
            () -> assertThat(productReportService.countManufacturerReports(now)).isEqualTo(2),
            () -> assertThat(productReportService.countProductStatusReports(now)).isEqualTo(1),
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

}