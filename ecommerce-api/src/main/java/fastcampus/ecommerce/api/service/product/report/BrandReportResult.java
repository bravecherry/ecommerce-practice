package fastcampus.ecommerce.api.service.product.report;

import fastcampus.ecommerce.api.domain.product.report.BrandReport;
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
public class BrandReportResult {

    private LocalDate statDate;
    private String brand;
    private Long productCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal maxSalesPrice;
    private BigDecimal minSalesPrice;
    private Integer totalStockQuantity;
    private BigDecimal avgStockQuantity;
    private BigDecimal potentialSalesAmount;

    public static BrandReportResult from(BrandReport brandReport) {
        return new BrandReportResult(
            brandReport.getStatDate(),
            brandReport.getBrand(),
            brandReport.getProductCount(),
            brandReport.getAvgSalesPrice(),
            brandReport.getMaxSalesPrice(),
            brandReport.getMinSalesPrice(),
            brandReport.getTotalStockQuantity(),
            brandReport.getAvgStockQuantity(),
            brandReport.getPotentialSalesAmount());
    }
}
