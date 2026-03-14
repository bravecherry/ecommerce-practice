package fastcampus.ecommerce.batch.jobconfig.transaction.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.ecommerce.batch.domain.transaction.TransactionReport;
import fastcampus.ecommerce.batch.domain.transaction.TransactionReportMapRepository;
import fastcampus.ecommerce.batch.dto.transaction.TransactionLog;
import fastcampus.ecommerce.batch.service.file.SplitFilePartitioner;
import fastcampus.ecommerce.batch.service.transaction.TransactionReportAccumulator;
import fastcampus.ecommerce.batch.util.FileUtils;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionReportJobConfiguration {

    // 스텝 분류
    // - 로그를 읽어들여서 집계
    // - 집계된 데이터를 DB에 저장
    @Bean
    public Job transactionReportJob(JobRepository jobRepository, JobExecutionListener listener,
        Step transactionAccumulatePartitionStep, Step transactionSaveStep) {
        return new JobBuilder("transactionReportJob", jobRepository)
            .start(transactionAccumulatePartitionStep)
            .next(transactionSaveStep)
            .listener(listener)
            .build();
    }

    @Bean
    public Step transactionAccumulatePartitionStep(JobRepository jobRepository,
        Step transactionAccumulateStep,
        SplitFilePartitioner splitLogFilePartitioner, PartitionHandler logFilePartitionHandler) {
        return new StepBuilder("transactionAccumulatePartitionStep", jobRepository)
            .partitioner(transactionAccumulateStep.getName(), splitLogFilePartitioner)
            .partitionHandler(logFilePartitionHandler)
            //allowStartIfComplete(true) Spring Batch에서 `return` StepBuilder을 사용하면
            // 이전에 성공적으로 완료된 단계라도 다시 실행하도록 지정할 수 있습니다.
            // 이는 재시작 시 작업 인스턴스당 한 번만 단계를 실행하도록 하는 기본 동작을 재정의하는 것입니다.
            // 재실행이 필요하거나 재시작 가능한 작업의 일부로 인해 다시 트리거될 수 있는 단계에 특히 중요하며,
            // 이를 통해 해당 단계는 실행 컨텍스트에서 "항상 실행 가능"하게 됩니다.
            //작동 방식
            //기본 동작 : 기본적으로 Spring Batch 단계는 작업 인스턴스당 한 번만 실행되도록 설계되었습니다.
            // 단계를 이미 완료한 상태에서 작업을 다시 시작하려고 하면 다시 실행되지 않습니다.
            //allowStartIfComplete(true) StepBuilder이 메서드를 (또는 와 같은 특수 빌더 에서) 호출하면
            // SimpleStepBuilder 이 규칙이 변경되어 이전 실행이 성공했더라도 단계가 다시 시작될 수 있습니다.
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    @JobScope // job parameters를 사용하려면 scope 설정이 되어있어야 한다.
    public SplitFilePartitioner splitLogFilePartitioner(
        @Value("#{jobParameters['inputFilePath']}") String path,
        @Value("#{jobParameters['gridSize']}") int gridSize
    ) {
        return new SplitFilePartitioner(FileUtils.splitLog(new File(path), gridSize));
    }

    @Bean
    @JobScope
    public TaskExecutorPartitionHandler logFilePartitionHandler(TaskExecutor taskExecutor,
        Step transactionAccumulateStep,
        @Value("#{jobParameters['gridSize']}") int gridSize) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(transactionAccumulateStep);
        handler.setGridSize(gridSize);
        return handler;
    }

    @Bean
    public Step transactionAccumulateStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        StepExecutionListener listener,
        ItemReader<TransactionLog> logReader,
        ItemWriter<TransactionLog> logWriter,
        TaskExecutor taskExecutor) {
        return new StepBuilder("transactionAccumulateStep", jobRepository)
            .<TransactionLog, TransactionLog>chunk(1000, transactionManager)
            .reader(logReader)
            .writer(logWriter)
            .allowStartIfComplete(true)
            .listener(listener)
            .taskExecutor(taskExecutor)
            .build();
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<TransactionLog> logReader(
        @Value("#{stepExecutionContext['file']}") File file, ObjectMapper objectMapper) {
        FlatFileItemReader<TransactionLog> itemReader = new FlatFileItemReaderBuilder<TransactionLog>()
            .name("logReader")
            .resource(new FileSystemResource(file))
            .lineMapper((line, lineNumber) -> objectMapper.readValue(line, TransactionLog.class))
            .build();
        return new SynchronizedItemStreamReaderBuilder<TransactionLog>()
            .delegate(itemReader)
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
    public JpaItemWriter<TransactionReport> reportWriter(EntityManagerFactory factory) {
        return new JpaItemWriterBuilder<TransactionReport>()
            .entityManagerFactory(factory)
            .usePersist(true)
            .build();
    }

}
