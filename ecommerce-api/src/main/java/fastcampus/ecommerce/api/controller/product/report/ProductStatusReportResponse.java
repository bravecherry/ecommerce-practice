package fastcampus.ecommerce.api.controller.product.report;

import fastcampus.ecommerce.api.service.product.report.ProductStatusReportResult;
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
public class ProductStatusReportResponse {

    private LocalDate statDate;
    private String productStatus;
    private Long productCount;
    private BigDecimal avgStockQuantity;

    public static ProductStatusReportResponse from(
        ProductStatusReportResult ProductStatusReportResult) {
        return new ProductStatusReportResponse(
            ProductStatusReportResult.getStatDate(),
            ProductStatusReportResult.getProductStatus(),
            ProductStatusReportResult.getProductCount(),
            ProductStatusReportResult.getAvgStockQuantity());
    }
}
