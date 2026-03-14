package fastcampus.ecommerce.batch.jobconfig.product.upload;

import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.dto.product.upload.ProductUploadCsvRow;
import fastcampus.ecommerce.batch.service.file.SplitFilePartitioner;
import fastcampus.ecommerce.batch.service.monitoring.BatchStepExecutionListener;
import fastcampus.ecommerce.batch.util.FileUtils;
import fastcampus.ecommerce.batch.util.ReflectionUtils;
import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
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
    public Job productUploadJob(JobRepository jobRepository, Step productUploadPartitionStep,
        JobExecutionListener listener) {
        return new JobBuilder("productUploadJob", jobRepository)
            .listener(listener)
            .start(productUploadPartitionStep)
            .build();
    }

    @Bean
    public Step productUploadPartitionStep(JobRepository jobRepository, Step productUploadStep,
        SplitFilePartitioner splitFilePartitioner, PartitionHandler splitFilePartitionHandler) {
        return new StepBuilder("productUploadPartitionStep", jobRepository)
            .partitioner(productUploadStep.getName(), splitFilePartitioner)
            .partitionHandler(splitFilePartitionHandler)
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
    public SplitFilePartitioner splitFilePartitioner(
        @Value("#{jobParameters['inputFilePath']}") String path,
        @Value("#{jobParameters['gridSize']}") int gridSize
    ) {
        return new SplitFilePartitioner(FileUtils.splitCsv(new File(path), gridSize));
    }

    @Bean
    @JobScope
    public TaskExecutorPartitionHandler splitFilePartitionHandler(TaskExecutor taskExecutor,
        Step productUploadStep, @Value("#{jobParameters['gridSize']}") int gridSize) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(productUploadStep);
        handler.setGridSize(gridSize);
        return handler;
    }

    //csv 파일을 읽어서 만들어진 row를 가지고 product를 변환해서 DB에 넣어주는데,
    //변환하는 부분은 item processor 구현체를 통해 logic 수행 예정
    @Bean
    public Step productUploadStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        BatchStepExecutionListener listen,
        ItemReader<ProductUploadCsvRow> reader,
        ItemProcessor<ProductUploadCsvRow, Product> processor,
        JpaItemWriter<Product> writer,
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
        @Value("#{stepExecutionContext['file']}") File file
    ) {
        FlatFileItemReader<ProductUploadCsvRow> reader = new FlatFileItemReaderBuilder<ProductUploadCsvRow>()
            .name("productReader")
            .resource(new FileSystemResource(file))
            .delimited()
            .names(ReflectionUtils.getFieldNames(ProductUploadCsvRow.class)
                .toArray(String[]::new))
            .targetType(ProductUploadCsvRow.class)
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
    public JpaItemWriter<Product> productWriter(EntityManagerFactory factory) {
        return new JpaItemWriterBuilder<Product>()
            .entityManagerFactory(factory)
            .usePersist(true)
            .build();
    }

}
