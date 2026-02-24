package fastcampus.ecommerce.api.domain.product.payment;

import fastcampus.ecommerce.api.domain.product.order.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Timestamp paymentDate;
    private Integer amount;

    @OneToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Order order;

    public static Payment createPayment(PaymentMethod paymentMethod, Integer amount, Order order) {
        return new Payment(null, paymentMethod, PaymentStatus.PENDING,
                new Timestamp(System.currentTimeMillis()), amount, order);
    }

    public void complete() {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalPaymentStateException("결제 대기 중에만 처리 가능합니다.");
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void fail() {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalPaymentStateException("결제 대기 중에만 처리 가능합니다.");
        }
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void cancel() {
        switch (this.paymentStatus) {
            case COMPLETED -> this.paymentStatus = PaymentStatus.REFUNDED;
            case PENDING, FAILED -> this.paymentStatus = PaymentStatus.CANCELLED;
            case CANCELLED -> throw new IllegalPaymentStateException("이미 취소가 완료된 건 입니다.");
            case REFUNDED -> throw new IllegalPaymentStateException("이미 환불이 완료된 건 입니다.");
        }
    }

    public boolean isSuccess() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

}
