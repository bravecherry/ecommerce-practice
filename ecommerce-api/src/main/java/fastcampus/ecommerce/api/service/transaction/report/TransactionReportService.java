package fastcampus.ecommerce.api.service.transaction.report;

import fastcampus.ecommerce.api.domain.transaction.report.TransactionReportRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionReportService {

    private final TransactionReportRepository transactionReportRepository;

    public TransactionReportListResult findByDate(LocalDate date) {
        return TransactionReportListResult.from(
            transactionReportRepository.findByTransactionDate(date));
    }

}
