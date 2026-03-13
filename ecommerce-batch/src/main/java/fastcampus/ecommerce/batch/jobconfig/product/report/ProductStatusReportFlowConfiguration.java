package fastcampus.ecommerce.batch.jobconfig.product.report;

import fastcampus.ecommerce.batch.domain.product.report.ProductStatusReport;
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
    public JdbcCursorItemReader<ProductStatusReport> productStatusReportItemReader(
        DataSource dataSource) {
        String sql = """
            select product_status,
                count(*) as product_count,
                avg(stock_quantity) as avg_stock_quantity
            from products
            group by product_status;
            """;
        return new JdbcCursorItemReaderBuilder<ProductStatusReport>()
            .dataSource(dataSource)
            .name("productStatusReportItemReader")
            .sql(sql)
            .beanRowMapper(ProductStatusReport.class)
            .build();
    }

    @Bean
    public JdbcBatchItemWriter<ProductStatusReport> productStatusReportItemWriter(
        DataSource dataSource) {
        String sql = "insert into product_status_reports "
            + "(stat_date, productStatus, product_count, avg_sales_price, max_sales_price, min_sales_price, "
            + "total_stock_quantity, avg_stock_quantity, potential_sales_amount) "
            + "values "
            + "(:statDate, :productStatus, :productCount, :avgSalesPrice, :maxSalesPrice, :minSalesPrice, "
            + ":totalStockQuantity, :avgStockQuantity, :potentialSalesAmount)";
        return new JdbcBatchItemWriterBuilder<ProductStatusReport>()
            .dataSource(dataSource)
            .sql(sql)
            .build();
    }

}
