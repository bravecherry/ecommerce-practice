package fastcampus.ecommerce.api.controller.product.report;

import fastcampus.ecommerce.api.service.product.report.ManufacturerReportResult;
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
public class ManufacturerReportResponse {

    private LocalDate statDate;
    private String manufacturer;
    private Long productCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal potentialSalesAmount;

    public static ManufacturerReportResponse from(ManufacturerReportResult manufacturerReport) {
        return new ManufacturerReportResponse(
            manufacturerReport.getStatDate(),
            manufacturerReport.getManufacturer(),
            manufacturerReport.getProductCount(),
            manufacturerReport.getAvgSalesPrice(),
            manufacturerReport.getPotentialSalesAmount());
    }
}
