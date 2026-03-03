package fastcampus.ecommerce.api.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("대기 중"),
    PROCESSING("처리 중"),
    COMPLETED("주문 완료"),
    CANCELLED("주문 취소"),
    PENDING_PAYMENT("결제 대기");

    final String desc;

    OrderStatus(String desc) {
        this.desc = desc;
    }
}
