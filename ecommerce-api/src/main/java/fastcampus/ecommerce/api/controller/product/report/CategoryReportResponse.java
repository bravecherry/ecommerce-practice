package fastcampus.ecommerce.api.controller.product.report;

import fastcampus.ecommerce.api.service.product.report.CategoryReportResult;
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
public class CategoryReportResponse {

    private LocalDate statDate;
    private String category;
    private Long productCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal maxSalesPrice;
    private BigDecimal minSalesPrice;
    private Integer totalStockQuantity;
    private BigDecimal potentialSalesAmount;

    public static CategoryReportResponse from(CategoryReportResult categoryReport) {
        return new CategoryReportResponse(
            categoryReport.getStatDate(),
            categoryReport.getCategory(),
            categoryReport.getProductCount(),
            categoryReport.getAvgSalesPrice(),
            categoryReport.getMaxSalesPrice(),
            categoryReport.getMinSalesPrice(),
            categoryReport.getTotalStockQuantity(),
            categoryReport.getPotentialSalesAmount());
    }
}
