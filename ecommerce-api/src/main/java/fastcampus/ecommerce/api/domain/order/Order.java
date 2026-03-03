package fastcampus.ecommerce.api.domain.order;

import fastcampus.ecommerce.api.domain.payment.Payment;
import fastcampus.ecommerce.api.domain.payment.PaymentMethod;
import fastcampus.ecommerce.api.domain.payment.PaymentStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Timestamp orderDate;
    private Long customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // cascade 설정: Order 를 통해 OrderItem 데이터도 함께 저장하기 위해 cascade 설정
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Payment payment;

    public static Order createOrder(Long customerId) {
        return new Order(null, new Timestamp(System.currentTimeMillis()), customerId,
                OrderStatus.PENDING_PAYMENT, new ArrayList<>(), null);
    }

    //생성과 관련된 로직이므로 테스트는 스킵하도록 한다,
    //원래대로라면 테스트를 추가하는게 맞지만 ... 그냥 하지 않는듯
    public OrderItem addOrderItem(String productId, Integer quantity, Integer unitPrice) {
        OrderItem orderItem = OrderItem.createOrderItem(productId, quantity, unitPrice, this);
        orderItems.add(orderItem);
        return orderItem;
    }

    public void initPayment(PaymentMethod paymentMethod) {
        payment = Payment.createPayment(paymentMethod, calculateTotalAmount(), null);
    }

    public void completePayment(boolean success) {
        if (orderStatus != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalOrderStateException("결제를 처리할 수 없습니다.");
        }
        if (success) {
            payment.complete();
        } else {
            payment.fail();
        }
        orderStatus = OrderStatus.PROCESSING;
    }

    public PaymentStatus getPaymentStatus() {
        return payment.getPaymentStatus();
    }

    public boolean isPaymentSuccess() {
        return payment.isSuccess();
    }

    public void complete() {
        if (orderStatus != OrderStatus.PROCESSING) {
            throw new IllegalOrderStateException("처리 중인 주문만 완료 가능합니다.");
        }
        if (!isPaymentSuccess()) {
            throw new IllegalOrderStateException("결제가 완료되지 않았습니다.");
        }
        orderStatus = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (orderStatus == OrderStatus.COMPLETED) {
            throw new IllegalOrderStateException("완료된 주문은 취소할 수 없습니다.");
        }
        payment.cancel();
        orderStatus = OrderStatus.CANCELLED;
    }

    public Integer calculateTotalAmount() {
        return orderItems.stream()
                .mapToInt(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }

    public Long countProducts() {
        return (long) orderItems.size();
    }

    public Long calculateToTotalItemQuantity() {
        return orderItems.stream()
                .mapToLong(OrderItem::getQuantity)
                .sum();
    }

    public Long getPaymentId() {
        return payment == null ? null : payment.getPaymentId();
    }

    public PaymentMethod getPaymentMethod() {
        return payment == null ? null : payment.getPaymentMethod();
    }

    public Timestamp getPaymentDate() {
        return payment == null ? null : payment.getPaymentDate();
    }
}
