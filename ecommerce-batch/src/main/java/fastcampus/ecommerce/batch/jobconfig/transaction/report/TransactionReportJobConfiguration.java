package fastcampus.ecommerce.batch.jobconfig.transaction.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.ecommerce.batch.domain.transaction.TransactionReport;
import fastcampus.ecommerce.batch.domain.transaction.TransactionReportMapRepository;
import fastcampus.ecommerce.batch.dto.transaction.TransactionLog;
import fastcampus.ecommerce.batch.service.transaction.TransactionReportAccumulator;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionReportJobConfiguration {

    // 스텝 분류
    // - 로그를 읽어들여서 집계
    // - 집계된 데이터를 DB에 저장
    @Bean
    public Job transactionReportJob(JobRepository jobRepository, JobExecutionListener listener,
        Step transactionAccumulateStep, Step transactionSaveStep) {
        return new JobBuilder("transactionReportJob", jobRepository)
            .start(transactionAccumulateStep)
            .next(transactionSaveStep)
            .listener(listener)
            .build();
    }

    @Bean
    public Step transactionAccumulateStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        StepExecutionListener listener,
        ItemReader<TransactionLog> logReader,
        ItemWriter<TransactionLog> logWriter) {
        return new StepBuilder("transactionAccumulateStep", jobRepository)
            .<TransactionLog, TransactionLog>chunk(1000, transactionManager)
            .reader(logReader)
            .writer(logWriter)
            .allowStartIfComplete(true)
            .listener(listener)
            .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionLog> logReader(
        @Value("#{jobParameters['inputFilePath']}") String path, ObjectMapper objectMapper) {
        return new FlatFileItemReaderBuilder<TransactionLog>()
            .name("logReader")
            .resource(new FileSystemResource(path))
            .lineMapper((line, lineNumber) -> objectMapper.readValue(line, TransactionLog.class))
            .build();
    }

    @Bean
    @StepScope
    public ItemWriter<TransactionLog> logWriter(TransactionReportAccumulator accumulator) {
        return chunk -> {
            for (TransactionLog transactionLog : chunk.getItems()) {
                accumulator.accumulate(transactionLog);
            }
        };
    }

    @Bean
    public Step transactionSaveStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        StepExecutionListener listener,
        ItemReader<TransactionReport> reportReader,
        ItemWriter<TransactionReport> reportWriter) {
        return new StepBuilder("transactionSaveStep", jobRepository)
            .<TransactionReport, TransactionReport>chunk(1000, transactionManager)
            .allowStartIfComplete(true)
            .reader(reportReader)
            .writer(reportWriter)
            .listener(listener)
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<TransactionReport> reportReader(TransactionReportMapRepository repository) {
        return new IteratorItemReader<>(repository.getTransactionReports());
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<TransactionReport> reportWriter(DataSource dataSource) {
        String sql = "insert into transaction_reports ("
            + "transaction_date, transaction_type, transaction_count, total_amount, "
            + "customer_count, order_count, payment_method_count, avg_product_count, "
            + "total_item_quantity) values ("
            + ":transactionDate, :transactionType, :transactionCount, :totalAmount, "
            + ":customerCount, :orderCount, :paymentMethodCount, :avgProductCount, "
            + ":totalItemQuantity)";
        return new JdbcBatchItemWriterBuilder<TransactionReport>()
            .dataSource(dataSource)
            .sql(sql)
            .beanMapped()
            .build();
    }

}
