package fastcampus.ecommerce.api.domain.transaction.report;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionReportRepository extends
    JpaRepository<TransactionReport, TransactionReportId> {

    List<TransactionReport> findByTransactionDate(LocalDate transactionDate);
}
