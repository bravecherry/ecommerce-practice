package fastcampus.ecommerce.batch.domain.product.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_reports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@IdClass(CategoryReportId.class)
public class CategoryReport implements Serializable {

    @Id
    private LocalDate statDate;
    @Id
    private String category;
    private Long productCount;
    private Double avgSalesPrice;
    private Integer maxSalesPrice;
    private Integer minSalesPrice;
    private Long totalStockQuantity;
    private Long potentialSalesAmount;

    public CategoryReport(String category, Long productCount, Double avgSalesPrice,
        Integer maxSalesPrice, Integer minSalesPrice, Long totalStockQuantity,
        Long potentialSalesAmount) {
        this(LocalDate.now(), category, productCount, avgSalesPrice, maxSalesPrice, minSalesPrice,
            totalStockQuantity, potentialSalesAmount);
    }
}
