package fastcampus.ecommerce.batch.jobconfig.product.report;

import fastcampus.ecommerce.batch.domain.product.report.BrandReport;
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
public class BrandReportFlowConfiguration {

    @Bean
    public Flow brandReportFlow(Step brandReportStep) {
        return new FlowBuilder<SimpleFlow>("brandReportFlow")
            .start(brandReportStep)
            .build();
    }

    @Bean
    public Step brandReportStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<BrandReport> brandReportItemReader,
        ItemWriter<BrandReport> brandReportItemWriter,
        StepExecutionListener listener) {
        return new StepBuilder("brandReportStep", jobRepository)
            .<BrandReport, BrandReport>chunk(10, transactionManager)
            .allowStartIfComplete(true)
            .listener(listener)
            .reader(brandReportItemReader)
            .writer(brandReportItemWriter)
            .build();
    }

    @Bean
    public JpaCursorItemReader<BrandReport> brandReportItemReader(EntityManagerFactory factory) {
        String sql = """
            select new BrandReport(p.brand,
                count(p),
                avg(p.salesPrice),
                max(p.salesPrice),
                min(p.salesPrice),
                sum(p.stockQuantity),
                avg(p.stockQuantity),
                sum(p.salesPrice * p.stockQuantity))
            from Product p
            group by p.brand
            """;
        return new JpaCursorItemReaderBuilder<BrandReport>()
            .name("brandReportItemReader")
            .entityManagerFactory(factory)
            .queryString(sql)
            .build();
    }

    @Bean
    public JpaItemWriter<BrandReport> brandReportItemWriter(EntityManagerFactory factory) {
        return new JpaItemWriterBuilder<BrandReport>()
            .entityManagerFactory(factory)
            .usePersist(true)
            .build();
    }

}
