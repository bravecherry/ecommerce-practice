package fastcampus.ecommerce.batch.service.file;

import static org.assertj.core.api.Assertions.assertThat;

import fastcampus.ecommerce.batch.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

class SplitFilePartitionerTest {

    @Test
    void testMultipleFiles() throws IOException {
        int gridSize = 3;
        List<File> files = createTmpFiles(gridSize);
        SplitFilePartitioner partitioner = new SplitFilePartitioner(files);
        // 이미 나눠져 있기 떄문에 인자 값은 의미 없다 .
        Map<String, ExecutionContext> result = partitioner.partition(gridSize);

        assertThat(result).hasSize(gridSize)
                .isEqualTo(Map.of(
                        "partition0", new ExecutionContext(Map.of("file", files.get(0))),
                        "partition1", new ExecutionContext(Map.of("file", files.get(1))),
                        "partition2", new ExecutionContext(Map.of("file", files.get(2)))
                ));
    }

    @Test
    void testEmpty() {
        SplitFilePartitioner partitioner = new SplitFilePartitioner(new ArrayList<>());
        Map<String, ExecutionContext> result = partitioner.partition(2);
        assertThat(result).isEmpty();
    }

    private static List<File> createTmpFiles(int fileCount) throws IOException {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            files.add(FileUtils.createTmpFile("test_" + i, ".tmp"));
        }
        return files;
    }
}