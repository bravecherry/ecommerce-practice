package fastcampus.ecommerce.api.domain.product.payment;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("신용카드"),
    DEBIT_CARD("직불카드"),
    PAYPAL("페이팔"),
    BANK_TRANSFER("계좌이체"),
    ;
    final String desc;

    PaymentMethod(String desc) {
        this.desc = desc;
    }
}
