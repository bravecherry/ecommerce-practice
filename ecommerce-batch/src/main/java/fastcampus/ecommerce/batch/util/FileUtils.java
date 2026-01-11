package fastcampus.ecommerce.batch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

    public static List<File> splitCsv(File csvFile, int fileCount) {
        long lineCount;
        try (Stream<String> stream = Files.lines(csvFile.toPath(), StandardCharsets.UTF_8)) {
            lineCount = stream.count();
            long linesPerFile = (long) Math.ceil((double) lineCount / fileCount);
            return splitFiles(csvFile, linesPerFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<File> splitFiles(File csvFile, long linesPerFile) {
        List<File> splitFiles = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFile.toPath(),
                StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;
            boolean shouldCreateFile = true;
            int fileIndex = 0;
            int lineCount = 0;
            File splitFile;
            BufferedWriter writer = null;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (shouldCreateFile) {
                    splitFile = createTmpFile("split_" + (fileIndex++) + "_", ".csv");
                    writer = Files.newBufferedWriter(splitFile.toPath(), StandardCharsets.UTF_8);
                    splitFiles.add(splitFile);
                    lineCount = 0;
                    shouldCreateFile = false;
                }
                writer.write(line);
                writer.newLine();
                lineCount++;

                if (lineCount >= linesPerFile) {
                    writer.close();
                    shouldCreateFile = true;
                }
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return splitFiles;
    }

    public static File createTmpFile(String prefix, String suffix) throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        //JVM 삭제 시 임시 파일도 함께 삭제
        tmpFile.deleteOnExit();
        return tmpFile;
    }
}
