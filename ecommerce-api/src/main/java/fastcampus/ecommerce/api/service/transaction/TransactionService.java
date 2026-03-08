package fastcampus.ecommerce.api.service.transaction;

import fastcampus.ecommerce.api.domain.order.OrderResult;
import fastcampus.ecommerce.api.domain.transaction.TransactionStatus;
import fastcampus.ecommerce.api.domain.transaction.TransactionType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final String NA = "N/A";

    public void logTransaction(TransactionType transactionType, TransactionStatus transactionStatus,
        String message, OrderResult order) {
        try {
            putMdc(transactionType, transactionStatus, order);
        } finally {
            MDC.clear();
        }
        log(transactionStatus, message);
    }

    private void putMdc(TransactionType transactionType, TransactionStatus transactionStatus,
        OrderResult order) {
        Optional.ofNullable(order)
            .ifPresentOrElse(this::putOrder, this::putNAOrder);
        putTransaction(transactionType, transactionStatus);
    }

    private void putTransaction(TransactionType transactionType,
        TransactionStatus transactionStatus) {
        MDC.put("transactionType", transactionType.name());
        MDC.put("transactionStatus", transactionStatus.name());
    }

    private void putNAOrder() {
        MDC.put("orderId", NA);
        MDC.put("customerId", NA);
        MDC.put("totalAmount", NA);
        MDC.put("paymentMethod", NA);
        MDC.put("productCount", NA);
        MDC.put("totalItemQuantity", NA);
    }

    private void putOrder(OrderResult order) {
        MDC.put("orderId", order.getOrderId().toString());
        MDC.put("customerId", order.getCustomerId().toString());
        MDC.put("totalAmount", order.getTotalAmount().toString());
        MDC.put("paymentMethod", order.getPaymentMethod().toString());
        MDC.put("productCount", order.getProductCount().toString());
        MDC.put("totalItemQuantity", order.getTotalItemQuantity().toString());
    }

    private void log(TransactionStatus transactionStatus, String message) {
        if (transactionStatus == TransactionStatus.SUCCESS) {
            logger.info(message);
        } else {
            logger.error(message);
        }
    }

}
