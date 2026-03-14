package fastcampus.ecommerce.api.controller.product.report;

import fastcampus.ecommerce.api.service.product.report.ProductReportListResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductReportResponse {

    private List<BrandReportResponse> brandReports;
    private List<CategoryReportResponse> categoryReports;
    private List<ManufacturerReportResponse> manufacturerReports;
    private List<ProductStatusReportResponse> productStatusReports;

    public static ProductReportResponse from(ProductReportListResult result) {
        return new ProductReportResponse(
            result.getBrandReports().stream().map(BrandReportResponse::from).toList(),
            result.getCategoryReports().stream().map(CategoryReportResponse::from).toList(),
            result.getManufacturerReports().stream().map(ManufacturerReportResponse::from).toList(),
            result.getProductStatusReports().stream().map(ProductStatusReportResponse::from)
                .toList());
    }
}
