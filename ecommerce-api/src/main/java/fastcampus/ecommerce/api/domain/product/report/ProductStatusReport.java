package fastcampus.ecommerce.api.domain.product.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_status_reports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@IdClass(ProductStatusReportId.class)
public class ProductStatusReport {

    @Id
    private LocalDate statDate;
    @Id
    private String productStatus;
    private Long productCount;
    private BigDecimal avgStockQuantity;
}
