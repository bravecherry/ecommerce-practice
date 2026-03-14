package fastcampus.ecommerce.batch.jobconfig.product.report;

import fastcampus.ecommerce.batch.domain.product.report.ProductStatusReport;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ProductStatusReportFlowConfiguration {

    @Bean
    public Flow productStatusReportFlow(Step productStatusReportStep) {
        return new FlowBuilder<SimpleFlow>("productStatusReportFlow")
            .start(productStatusReportStep)
            .build();
    }

    @Bean
    public Step productStatusReportStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<ProductStatusReport> productStatusReportItemReader,
        ItemWriter<ProductStatusReport> productStatusReportItemWriter,
        StepExecutionListener listener) {
        return new StepBuilder("productStatusReportStep", jobRepository)
            .<ProductStatusReport, ProductStatusReport>chunk(10, transactionManager)
            .allowStartIfComplete(true)
            .listener(listener)
            .reader(productStatusReportItemReader)
            .writer(productStatusReportItemWriter)
            .build();
    }

    @Bean
    public JpaCursorItemReader<ProductStatusReport> productStatusReportItemReader(
        EntityManagerFactory factory) {
        String sql = """
            select new ProductStatusReport(p.productStatus,
                count(p),
                avg(p.stockQuantity))
            from Product p
            group by p.productStatus
            """;
        return new JpaCursorItemReaderBuilder<ProductStatusReport>()
            .name("productStatusReportItemReader")
            .entityManagerFactory(factory)
            .queryString(sql)
            .build();
    }

    @Bean
    public JpaItemWriter<ProductStatusReport> productStatusReportItemWriter(
        EntityManagerFactory factory) {
        return new JpaItemWriterBuilder<ProductStatusReport>()
            .entityManagerFactory(factory)
            .usePersist(true)
            .build();
    }

}
