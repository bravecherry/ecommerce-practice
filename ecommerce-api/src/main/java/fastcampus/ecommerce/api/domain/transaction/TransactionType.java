package fastcampus.ecommerce.api.domain.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    ORDER_CREATION,
    PAYMENT_COMPLETION,
    ORDER_COMPLETION,
    ORDER_CANCELLATION;
}
