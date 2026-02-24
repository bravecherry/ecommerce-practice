package fastcampus.ecommerce.api.domain.product.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("결제 대기"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소"),
    REFUNDED("환불"),
    ;
    final String desc;

    PaymentStatus(String desc) {
        this.desc = desc;
    }
}
