package fastcampus.ecommerce.api.service.product.report;

import fastcampus.ecommerce.api.domain.product.report.CategoryReport;
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
public class CategoryReportResult {

    private LocalDate statDate;
    private String category;
    private Long productCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal maxSalesPrice;
    private BigDecimal minSalesPrice;
    private Integer totalStockQuantity;
    private BigDecimal potentialSalesAmount;

    public static CategoryReportResult from(CategoryReport categoryReport) {
        return new CategoryReportResult(
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
