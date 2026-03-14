package fastcampus.ecommerce.api.domain.product.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "brand_reports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@IdClass(BrandReportId.class)
public class BrandReport implements Serializable {

    @Id
    private LocalDate statDate;
    @Id
    private String brand;
    private Long productCount;
    private BigDecimal avgSalesPrice;
    private BigDecimal maxSalesPrice;
    private BigDecimal minSalesPrice;
    private Integer totalStockQuantity;
    private BigDecimal avgStockQuantity;
    private BigDecimal potentialSalesAmount;
}
