package fastcampus.ecommerce.api.service.product.report;

import fastcampus.ecommerce.api.domain.product.report.ProductStatusReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductStatusReportResult {

    private LocalDate statDate;
    private String productStatus;
    private Long productCount;
    private BigDecimal avgStockQuantity;

    public static ProductStatusReportResult from(ProductStatusReport ProductStatusReportResult) {
        return new ProductStatusReportResult(
            ProductStatusReportResult.getStatDate(),
            ProductStatusReportResult.getProductStatus(),
            ProductStatusReportResult.getProductCount(),
            ProductStatusReportResult.getAvgStockQuantity());
    }
}
