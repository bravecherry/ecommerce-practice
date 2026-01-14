package fastcampus.ecommerce.batch.jobconfig.product.download;

import fastcampus.ecommerce.batch.domain.product.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("productPagingStep", jobRepository)
                .<Product, Product>chunk(100, transactionManager)
                .listener(listener)
                .build();
    }
}
