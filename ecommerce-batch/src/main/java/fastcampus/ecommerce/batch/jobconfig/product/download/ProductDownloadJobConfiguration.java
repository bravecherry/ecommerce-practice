package fastcampus.ecommerce.batch.jobconfig.product.download;

import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.dto.product.download.ProductDownloadCsvRow;
import fastcampus.ecommerce.batch.util.ReflectionUtils;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ProductDownloadJobConfiguration {

    // products 데이터가 많으므로 chunk 단위로 나눠서 데이터를 읽어온 다음 파일에 저장
    @Bean
    public Job productDownloadJob(JobRepository jobRepository, Step productPagingStep,
            JobExecutionListener listener) {
        return new JobBuilder("productDownloadJob", jobRepository)
                .start(productPagingStep)
                .listener(listener)
                .build();
    }

    @Bean
    public Step productPagingStep(JobRepository jobRepository, StepExecutionListener listener,
            PlatformTransactionManager transactionManager,
            ItemReader<Product> productPagingReader,
            ItemProcessor<Product, ProductDownloadCsvRow> productDownloadProcessor,
            ItemWriter<ProductDownloadCsvRow> productCsvWriter, TaskExecutor taskExecutor) {
        return new StepBuilder("productPagingStep", jobRepository)
                // DataSourceTransactionManager 가 아닌 이를 추상화한 PlatformTransactionManager 인터페이스를 사용하는 이유
                // 테스트 환경에서 트랜잭션 매니져를 가져올 떄나 실제로 DB에서 가져올 때 모두 동작하게 하기 위함
                .<Product, ProductDownloadCsvRow>chunk(100, transactionManager)
                .reader(productPagingReader)
                .processor(productDownloadProcessor)
                .writer(productCsvWriter)
                .listener(listener)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Product> productPagingReader(DataSource dataSource,
            PagingQueryProvider productPagingQueryProvider) {
        return new JdbcPagingItemReaderBuilder<Product>()
                .dataSource(dataSource)
                .name("productPagingReader")
                .queryProvider(productPagingQueryProvider)
                .pageSize(1000)
                .beanRowMapper(Product.class)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean productPagingQueryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("select product_id, seller_id, category, product_name, "
                + "sales_start_date, sales_end_date, product_status, brand, manufacturer, "
                + "sales_price, stock_quantity, created_at, updated_at");
        provider.setFromClause("from products");
        provider.setSortKey("product_id");
        return provider;
    }

    @Bean
    public ItemProcessor<Product, ProductDownloadCsvRow> productDownloadProcessor() {
        return ProductDownloadCsvRow::from;
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamWriter<ProductDownloadCsvRow> productCsvWriter(
            @Value("#{jobParameters['outputFilePath']}") String path) {
        List<String> columns = ReflectionUtils.getFieldNames(ProductDownloadCsvRow.class);
        FlatFileItemWriter<ProductDownloadCsvRow> productCsvWriter = new FlatFileItemWriterBuilder<ProductDownloadCsvRow>()
                .name("productCsvWriter")
                .resource(new FileSystemResource(path))
                .delimited()
                .names(columns.toArray(String[]::new))
                .headerCallback(writer -> writer.write(String.join(",", columns)))
                .build();

        return new SynchronizedItemStreamWriterBuilder<ProductDownloadCsvRow>()
                .delegate(productCsvWriter)
                .build();
    }
}
