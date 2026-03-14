package fastcampus.ecommerce.api.service.product.report;

import fastcampus.ecommerce.api.domain.product.report.ManufacturerReport;
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
public class ManufacturerReportResult {

    private LocalDate statDate;
    private String manufacturer;
    private Long productCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal potentialSalesAmount;

    public static ManufacturerReportResult from(ManufacturerReport manufacturerReport) {
        return new ManufacturerReportResult(
            manufacturerReport.getStatDate(),
            manufacturerReport.getManufacturer(),
            manufacturerReport.getProductCount(),
            manufacturerReport.getAvgSalesPrice(),
            manufacturerReport.getPotentialSalesAmount());
    }
}
