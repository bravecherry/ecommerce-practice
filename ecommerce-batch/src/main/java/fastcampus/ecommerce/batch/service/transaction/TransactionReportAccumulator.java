package fastcampus.ecommerce.batch.service.transaction;

import fastcampus.ecommerce.batch.domain.transaction.TransactionReport;
import fastcampus.ecommerce.batch.domain.transaction.TransactionReportMapRepository;
import fastcampus.ecommerce.batch.dto.transaction.TransactionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionReportAccumulator {

    private final TransactionReportMapRepository repository;

    public void accumulate(TransactionLog transactionLog) {
        if (!transactionLog.getTransactionStatus().equalsIgnoreCase("success")) {
            return;
        }
        repository.put(TransactionReport.from(transactionLog));
    }

}
