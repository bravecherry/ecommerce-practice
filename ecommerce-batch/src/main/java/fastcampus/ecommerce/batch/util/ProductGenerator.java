package fastcampus.ecommerce.batch.util;

import fastcampus.ecommerce.batch.domain.product.ProductStatus;
import fastcampus.ecommerce.batch.dto.product.upload.ProductUploadCsvRow;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

@Slf4j
public class ProductGenerator {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        String csvFilePath = "data/random_product.csv";
        int recordCount = 10_000_000;
        try (
                FileWriter fileWriter = new FileWriter(csvFilePath);
                CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.builder()
                        .setHeader(
                                ReflectionUtils.getFieldNames(ProductUploadCsvRow.class)
                                        .toArray(String[]::new))
                        .build());
        ) {
            for (int i = 0; i < recordCount; i++) {
                printer.printRecord(generateRecord());
                if (i % 100_000 == 0) {
                    log.info("Generated {}-th record", i);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static Object[] generateRecord() {
        ProductUploadCsvRow row = randomProductRow();
        return new Object[]{
                row.getSellerId(),
                row.getCategory(),
                row.getProductName(),
                row.getSalesStartDate(),
                row.getSalesEndDate(),
                row.getProductStatus(),
                row.getBrand(),
                row.getManufacturer(),
                row.getSalesPrice(),
                row.getStockQuantity(),
        };
    }

    private static ProductUploadCsvRow randomProductRow() {
        String[] CATEGORIES = {"IT/기술", "건강/의학", "금융/경제", "여행/레저", "음식/요리", "뷰티/패션", "엔터테인먼트",
                "교육/자기계발", "스포츠/레저", "라이프스타일/취미"};
        String[] PRODUCT_NAMES = {"스마트폰", "노트북", "스니커즈", "페인트", "비누", "화장품", "카펫", "장난감", "조명기기",
                "수송기계"};
        String[] BRANDS = {"삼성", "현대", "LG", "아모레퍼시픽", "농심", "오뚜기", "네이버", "카카오", "SK텔레콤", "KT"};
        String[] MANUFACTURERS = {"삼성전자", "현대자동차", "SK하이닉스", "LG전자", "POSCO", "현대중공업", "기아",
                "삼성SDI", "한국전력공사", "KB금융그룹"};
        String[] PRODUCT_STATUSES = Arrays.stream(ProductStatus.values()).map(Enum::name)
                .toArray(String[]::new);
        return ProductUploadCsvRow.of(
                getRandomSellerId(),
                randomChoice(CATEGORIES),
                randomChoice(PRODUCT_NAMES),
                randomDate(2020, 2022),
                randomDate(2023, 2025),
                randomChoice(PRODUCT_STATUSES),
                randomChoice(BRANDS),
                randomChoice(MANUFACTURERS),
                randomSalesPrice(),
                randomStockQuantity()
        );
    }

    private static int randomStockQuantity() {
        return RANDOM.nextInt(1, 1_001);
    }

    private static int randomSalesPrice() {
        return RANDOM.nextInt(10_000, 500_001);
    }

    private static String randomDate(int startYear, int endYear) {
        int year = RANDOM.nextInt(startYear, endYear + 1);
        int month = RANDOM.nextInt(1, 13);
        int day = RANDOM.nextInt(1, 29);
        return LocalDate.of(year, month, day).toString();
    }

    private static String randomChoice(String[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    private static Long getRandomSellerId() {
        return RANDOM.nextLong(1, 101);
    }

}
