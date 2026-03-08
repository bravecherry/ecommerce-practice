package fastcampus.ecommerce.api.service.transaction.report;

import fastcampus.ecommerce.api.domain.transaction.report.TransactionReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionReportListResult {

    private List<TransactionReportResult> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TransactionReportResult {

        private LocalDate transactionDate;
        private String transactionType;
        private Long transactionCount;
        private Long totalAmount;
        private Long customerCount;
        private Long orderCount;
        private Long paymentMethodCount;
        private BigDecimal avgProductCount;
        private Long totalItemQuantity;

        public static TransactionReportResult from(TransactionReport report) {
            return new TransactionReportResult(
                report.getTransactionDate(),
                report.getTransactionType(),
                report.getTransactionCount(),
                report.getTotalAmount(),
                report.getCustomerCount(),
                report.getOrderCount(),
                report.getPaymentMethodCount(),
                report.getAvgProductCount(),
                report.getTotalItemQuantity()
            );
        }

    }

    public static TransactionReportListResult from(List<TransactionReport> list) {
        return new TransactionReportListResult(list.stream()
            .map(TransactionReportResult::from)
            .collect(Collectors.toList()));
    }

}
