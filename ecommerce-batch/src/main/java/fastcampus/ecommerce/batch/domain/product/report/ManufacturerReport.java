package fastcampus.ecommerce.batch.domain.product.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 만약 재처리가 가능하게 하려면 일별로 재품에 대한 스냅샷을 떠서 저장해두어야 한다.
@Getter
@Setter
@NoArgsConstructor
public class ManufacturerReport {

    // 배치가 실행된 날짜
    private LocalDate statDate = LocalDate.now();
    private String manufacturer;
    private Integer productCount;
    private BigDecimal avgSalesPrice;
    private Integer potentialSalesAmount;
}
