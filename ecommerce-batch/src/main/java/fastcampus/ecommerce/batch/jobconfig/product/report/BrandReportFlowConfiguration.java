package fastcampus.ecommerce.batch.jobconfig.product.report;

import fastcampus.ecommerce.batch.domain.product.report.BrandReport;
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
    public JdbcCursorItemReader<BrandReport> brandReportItemReader(DataSource dataSource) {
        String sql = """
            select brand,
                count(*) as product_count,
                avg(sales_price) as avg_sales_price,
                max(sales_price) as max_sales_price,
                min(sales_price) as min_sales_price,
                sum(stock_quantity) as total_stock_quantity,
                avg(stock_quantity) as avg_stock_quantity,
                sum(sales_price * stock_quantity) as potential_sales_amount
            from products
            group by brand
            """;
        return new JdbcCursorItemReaderBuilder<BrandReport>()
            .dataSource(dataSource)
            .name("brandReportItemReader")
            .sql(sql)
            .beanRowMapper(BrandReport.class)
            .build();
    }

    @Bean
    public JdbcBatchItemWriter<BrandReport> brandReportItemWriter(DataSource dataSource) {
        String sql = "insert into brand_reports "
            + "(stat_date, brand, product_count, avg_sales_price, max_sales_price, min_sales_price, "
            + "total_stock_quantity, avg_stock_quantity, potential_sales_amount) "
            + "values "
            + "(:statDate, :brand, :productCount, :avgSalesPrice, :maxSalesPrice, :minSalesPrice, "
            + ":totalStockQuantity, :avgStockQuantity, :potentialSalesAmount)";
        return new JdbcBatchItemWriterBuilder<BrandReport>()
            .dataSource(dataSource)
            .sql(sql)
            .build();
    }

}
