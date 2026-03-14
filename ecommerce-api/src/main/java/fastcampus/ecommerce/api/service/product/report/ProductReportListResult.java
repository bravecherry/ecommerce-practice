package fastcampus.ecommerce.api.service.product.report;

import fastcampus.ecommerce.api.domain.product.report.BrandReport;
import fastcampus.ecommerce.api.domain.product.report.CategoryReport;
import fastcampus.ecommerce.api.domain.product.report.ManufacturerReport;
import fastcampus.ecommerce.api.domain.product.report.ProductStatusReport;
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
public class ProductReportListResult {

    private List<BrandReportResult> brandReports;
    private List<CategoryReportResult> categoryReports;
    private List<ManufacturerReportResult> manufacturerReports;
    private List<ProductStatusReportResult> productStatusReports;

    public static ProductReportListResult of(List<BrandReport> brandReportList,
        List<CategoryReport> categoryReportList, List<ManufacturerReport> manufacturerReportList,
        List<ProductStatusReport> productStatusReportList) {
        return new ProductReportListResult(
            brandReportList.stream().map(BrandReportResult::from).toList(),
            categoryReportList.stream().map(CategoryReportResult::from).toList(),
            manufacturerReportList.stream().map(ManufacturerReportResult::from).toList(),
            productStatusReportList.stream().map(ProductStatusReportResult::from).toList());
    }
}
