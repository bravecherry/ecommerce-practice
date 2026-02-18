package fastcampus.ecommerce.batch.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fastcampus.ecommerce.batch.domain.file.PartitionedFileRepository;
import fastcampus.ecommerce.batch.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;

@ExtendWith(MockitoExtension.class)
class ProductDownloadPartitionerTest {

    @Mock
    private ProductService productService;

    @Mock
    private PartitionedFileRepository repository;

    private ProductDownloadPartitioner partitioner;
    private File partitionedFile;

    @BeforeEach
    void setUp() throws IOException {
        partitioner = new ProductDownloadPartitioner(productService,
                repository);
        partitionedFile = FileUtils.createTmpFile("partition", ".csv");
    }

    @Test
    void testMultipleIds() throws IOException {
        List<String> productIds = Arrays.asList("1", "2", "3", "4", "5");
        when(productService.getProductIds()).thenReturn(productIds);
        when(repository.putFile(anyString(), anyString(), anyString())).thenReturn(partitionedFile);

        Map<String, ExecutionContext> result = partitioner.partition(2);

        assertAll(
                () -> assertThat(result).isEqualTo(Map.of(
                        "partition0",
                        new ExecutionContext(
                                Map.of("minId", "1", "maxId", "3", "file", partitionedFile)),
                        "partition1",
                        new ExecutionContext(
                                Map.of("minId", "4", "maxId", "5", "file", partitionedFile))
                )),
                () -> verify(repository, times(2)).putFile(anyString(), anyString(), anyString())
        );
    }
}