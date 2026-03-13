package fastcampus.ecommerce.batch.jobconfig.product.report;

import fastcampus.ecommerce.batch.domain.product.report.CategoryReport;
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
public class CategoryReportFlowConfiguration {

    @Bean
    public Flow categoryReportFlow(Step categoryReportStep) {
        return new FlowBuilder<SimpleFlow>("categoryReportFlow")
            .start(categoryReportStep)
            .build();
    }

    @Bean
    public Step categoryReportStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<CategoryReport> categoryReportItemReader,
        ItemWriter<CategoryReport> categoryReportItemWriter,
        StepExecutionListener listener) {
        return new StepBuilder("categoryReportStep", jobRepository)
            .<CategoryReport, CategoryReport>chunk(10, transactionManager)
            .allowStartIfComplete(true)
            .listener(listener)
            .reader(categoryReportItemReader)
            .writer(categoryReportItemWriter)
            .build();
    }

    @Bean
    public JdbcCursorItemReader<CategoryReport> categoryReportItemReader(DataSource dataSource) {
        String sql = """
            select category,
                count(*) as product_count,
                avg(sales_price) as avg_sales_price,
                max(sales_price) as max_sales_price,
                min(sales_price) as min_sales_price,
                sum(stock_quantity) as total_stock_quantity,
                sum(sales_price * stock_quantity) as potential_sales_amount
            from products
            group by category
            """;
        return new JdbcCursorItemReaderBuilder<CategoryReport>()
            .dataSource(dataSource)
            .name("categoryReportItemReader")
            .sql(sql)
            .beanRowMapper(CategoryReport.class)
            .build();
    }

    @Bean
    public JdbcBatchItemWriter<CategoryReport> categoryReportItemWriter(DataSource dataSource) {
        String sql = "insert into category_reports "
            + "(stat_date, category, product_count, avg_sales_price, max_sales_price, min_sales_price, "
            + "total_stock_quantity, potential_sales_amount) "
            + "values "
            + "(:statDate, :category, :productCount, :avgSalesPrice, :maxSalesPrice, :minSalesPrice, "
            + ":totalStockQuantity, :potentialSalesAmount)";
        return new JdbcBatchItemWriterBuilder<CategoryReport>()
            .dataSource(dataSource)
            .sql(sql)
            .build();
    }

}
