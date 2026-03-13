package fastcampus.ecommerce.batch.jobconfig.product.report;

import fastcampus.ecommerce.batch.domain.product.report.ManufacturerReport;
import javax.sql.DataSource;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ManufacturerReportFlowConfiguration {

    @Bean
    public Flow manufacturerReportFlow(Step manufacturerReportStep) {
        return new FlowBuilder<SimpleFlow>("manufacturerReportFlow")
            .start(manufacturerReportStep)
            .build();
    }

    @Bean
    public Step manufacturerReportStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<ManufacturerReport> manufacturerReportItemReader,
        ItemWriter<ManufacturerReport> manufacturerReportItemWriter,
        StepExecutionListener listener) {
        return new StepBuilder("manufacturerReportStep", jobRepository)
            .<ManufacturerReport, ManufacturerReport>chunk(10, transactionManager)
            .allowStartIfComplete(true)
            .reader(manufacturerReportItemReader)
            .writer(manufacturerReportItemWriter)
            .listener(listener)

            .build();
    }

    @Bean
    public JdbcCursorItemReader<ManufacturerReport> manufacturerReportItemReader(
        DataSource dataSource) {
        String sql = """
            select manufacturer,
                count(*) as product_count,
                avg(sales_price) as avg_sales_price,
                sum(sales_price * stock_quantity) as potential_sales_amount
            from products
            group by manufacturer
            """;
        return new JdbcCursorItemReaderBuilder<ManufacturerReport>()
            .dataSource(dataSource)
            .name("manufacturerReportItemReader")
            .sql(sql)
            .beanRowMapper(ManufacturerReport.class)
            .build();
    }

    @Bean
    public JdbcBatchItemWriter<ManufacturerReport> manufacturerReportItemWriter(
        DataSource dataSource) {
        String sql = "insert into manufacturer_reports "
            + "(stat_date, manufacturer, product_count, avg_sales_price, potential_sales_amount) "
            + "values "
            + "(:statDate, :manufacturer, :productCount, :avgSalesPrice, :potentialSalesAmount)";
        return new JdbcBatchItemWriterBuilder<ManufacturerReport>()
            .dataSource(dataSource)
            .sql(sql)
            .build();
    }

}
