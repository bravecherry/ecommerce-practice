package fastcampus.ecommerce.api.controller.transaction.report;

import fastcampus.ecommerce.api.service.transaction.report.TransactionReportListResult;
import fastcampus.ecommerce.api.service.transaction.report.TransactionReportListResult.TransactionReportResult;
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
public class TransactionReportListResponse {

    private List<TransactionReportResponse> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TransactionReportResponse {

        private LocalDate transactionDate;
        private String transactionType;
        private Long transactionCount;
        private Long totalAmount;
        private Long customerCount;
        private Long orderCount;
        private Long paymentMethodCount;
        private BigDecimal avgProductCount;
        private Long totalItemQuantity;

        public static TransactionReportResponse from(
            TransactionReportResult report) {
            return new TransactionReportResponse(
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

    public static TransactionReportListResponse from(TransactionReportListResult result) {
        return new TransactionReportListResponse(result.getList().stream()
            .map(TransactionReportResponse::from)
            .collect(Collectors.toList()));
    }
}
