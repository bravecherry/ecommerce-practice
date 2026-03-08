package fastcampus.ecommerce.api.controller.transaction.report;

import fastcampus.ecommerce.api.service.transaction.report.TransactionReportService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transaction/reports")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionReportService transactionReportService;

    @GetMapping("")
    public TransactionReportListResponse getTransactionReportList(
        @RequestParam("dt") @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        return TransactionReportListResponse.from(transactionReportService.findByDate(date));
    }
}
