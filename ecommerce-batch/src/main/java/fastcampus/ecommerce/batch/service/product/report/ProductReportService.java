package fastcampus.ecommerce.batch.service.product.report;

import fastcampus.ecommerce.batch.domain.product.report.BrandReportRepository;
import fastcampus.ecommerce.batch.domain.product.report.CategoryReportRepository;
import fastcampus.ecommerce.batch.domain.product.report.ManufacturerReportRepository;
import fastcampus.ecommerce.batch.domain.product.report.ProductStatusReportRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReportService {

    private final ProductStatusReportRepository productStatusReportRepository;
    private final ManufacturerReportRepository manufacturerReportRepository;
    private final BrandReportRepository brandReportRepository;
    private final CategoryReportRepository categoryReportRepository;

    public Long countProductStatusReports(LocalDate date) {
        return productStatusReportRepository.countByStatDate(date);
    }

    public Long countManufacturerReports(LocalDate date) {
        return manufacturerReportRepository.countByStatDate(date);
    }

    public Long countBrandReports(LocalDate date) {
        return brandReportRepository.countByStatDate(date);
    }

    public Long countCategoryReports(LocalDate date) {
        return categoryReportRepository.countByStatDate(date);
    }

}
