package fastcampus.ecommerce.batch.service.product.report;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReportService {

    private final JdbcTemplate jdbcTemplate;

    public Long countProductStatusReports(LocalDate now) {
        return jdbcTemplate.queryForObject(
            "select count(*) from product_status_reports where stat_date = '" + now + "'",
            Long.class);
    }

    public Long countManufacturerReports(LocalDate now) {
        return jdbcTemplate.queryForObject(
            "select count(*) from manufacturer_reports where stat_date = '" + now + "'",
            Long.class);
    }

    public Long countBrandReports(LocalDate now) {
        return jdbcTemplate.queryForObject(
            "select count(*) from brand_reports where stat_date = '" + now + "'",
            Long.class);
    }

    public Long countCategoryReports(LocalDate now) {
        return jdbcTemplate.queryForObject(
            "select count(*) from category_reports where stat_date = '" + now + "'",
            Long.class);
    }

}
