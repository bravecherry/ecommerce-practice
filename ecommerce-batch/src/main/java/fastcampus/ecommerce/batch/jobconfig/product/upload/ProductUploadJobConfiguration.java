package fastcampus.ecommerce.batch.jobconfig.product.upload;

import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.dto.product.upload.ProductUploadCsvRow;
import fastcampus.ecommerce.batch.service.monitoring.BatchStepExecutionListener;
import fastcampus.ecommerce.batch.util.ReflectionUtils;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ProductUploadJobConfiguration {

    @Bean
    public Job productUploadJob(JobRepository jobRepository, Step productUploadStep,
            JobExecutionListener listener) {
        return new JobBuilder("productUploadJob", jobRepository)
                .listener(listener)
                .start(productUploadStep)
                .build();
    }

    //csv 파일을 읽어서 만들어진 row를 가지고 product를 변환해서 DB에 넣어주는데,
    //변환하는 부분은 item processor 구현체를 통해 logic 수행 예정
    @Bean
    public Step productUploadStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            BatchStepExecutionListener listen,
            ItemReader<ProductUploadCsvRow> reader,
            ItemProcessor<ProductUploadCsvRow, Product> processor,
            ItemWriter<Product> writer,
            TaskExecutor taskExecutor) {
        return new StepBuilder("productUploadStep", jobRepository)
                //chunk 단위로 스레드가 병렬로 실행
                .<ProductUploadCsvRow, Product>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                //완료 후 계속 재실행할 수 있도록 해주는 옵션
                //운영환경에서는 job parameter에서 한번만 돌리고,  오류가 있을때만 따로 돌리게 하기위해 false 설정
                .allowStartIfComplete(true)
                .listener(listen)
                .taskExecutor(taskExecutor)
                .build();
    }

    //thread-safe 조치
    // FlatFileItemReader 는 thread-safe한 reader가 아니어서 동시성 문제를 방지하기 위해 수정해줘야 한다
    //한 스레드가 하나의 파일에 락을 걸고 일정 시간만 점유하도록 설정
    //FlatFileItemReader > SynchronizedItemStreamReader
    @Bean
    @StepScope
    public SynchronizedItemStreamReader<ProductUploadCsvRow> productUploadItemReader(
            @Value("#{jobParameters['inputFilePath']}") String path
    ) {
        FlatFileItemReader<ProductUploadCsvRow> reader = new FlatFileItemReaderBuilder<ProductUploadCsvRow>()
                .name("productReader")
                .resource(new FileSystemResource(path))
                .delimited()
                .names(ReflectionUtils.getFieldNames(ProductUploadCsvRow.class)
                        .toArray(String[]::new))
                .targetType(ProductUploadCsvRow.class)
                .linesToSkip(1)
                .build();

        return new SynchronizedItemStreamReaderBuilder<ProductUploadCsvRow>()
                .delegate(reader)
                .build();
    }

    @Bean
    public ItemProcessor<ProductUploadCsvRow, Product> productProcessor() {
        return Product::from;
    }

    @Bean
    public ItemWriter<Product> productWriter(DataSource dataSource) {
        String sql = "insert into products (product_id, seller_id, category, product_name, "
                + "sales_start_date, sales_end_date, product_status, brand, manufacturer, sales_price, "
                + "stock_quantity, created_at, updated_at ) values (:productId, :sellerId, :category, :productName, "
                + ":salesStartDate, :salesEndDate, :productStatus, :brand, :manufacturer, :salesPrice, "
                + ":stockQuantity, :createdAt, :updatedAt)";
        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }
}
