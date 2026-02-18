package fastcampus.ecommerce.batch.util;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FileUtilsTest {

    @Value("classpath:/data/products_for_upload.csv")
    private Resource csvResource;

    @TempDir
    private Path tempDir;

    @Test
    void splitCsv() throws IOException {
        List<File> files = FileUtils.splitCsv(csvResource.getFile(), 2);
        assertThat(files).hasSize(2);
        files.stream().map(File::getName).forEach(System.out::printf);
    }

    @Test
    void createTempFile() throws IOException {
        File file = FileUtils.createTmpFile("prefix", "suffix");
        assertThat(file.exists()).isTrue();
    }

    @Test
    void testMergeFiles() throws IOException {
        File file1 = createFile("filename1.csv", "Content 1\n");
        File file2 = createFile("filename2.csv", "Content 2\n");
        File file3 = createFile("filename3.csv", "Content 3\n");
        File outputFile = new File(tempDir.toFile(), "output.txt");
        String header = "content";

        FileUtils.mergeFiles(header, List.of(file1, file2, file3), outputFile);

        assertThat(Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8)).containsExactly(
                header, "Content 1", "Content 2", "Content 3");
    }

    private File createFile(String fileName, String content) throws IOException {
        File file = new File(tempDir.toFile(), fileName);
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

}