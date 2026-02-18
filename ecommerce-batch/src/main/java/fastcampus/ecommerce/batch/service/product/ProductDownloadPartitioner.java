package fastcampus.ecommerce.batch.service.product;

import fastcampus.ecommerce.batch.domain.file.PartitionedFileRepository;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDownloadPartitioner implements Partitioner {

    private final ProductService productService;
    private final PartitionedFileRepository repository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<String> productIds = productService.getProductIds().stream()
                .sorted().toList();

        int minId = 0;
        int maxId = productIds.size() - 1;
        int targetSize = (maxId - minId) / gridSize + 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        int start = minId;
        int end = start + targetSize - 1;

        int partitionNumber = 0;
        while (start <= maxId) {
            ExecutionContext context = new ExecutionContext();
            String partitionKey = "partition" + partitionNumber;
            result.put(partitionKey, context);

            if (end >= maxId) {
                end = maxId;
            }

            context.putString("minId", productIds.get(start));
            context.putString("maxId", productIds.get(end));

            try {
                File partitionedFile = repository.putFile(partitionKey,
                        "partition" + partitionNumber + "_", ".csv");
                context.put("file", partitionedFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            start += targetSize;
            end += targetSize;
            partitionNumber++;
        }

        return result;
    }
}
