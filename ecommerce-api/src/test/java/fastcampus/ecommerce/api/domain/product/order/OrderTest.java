package fastcampus.ecommerce.api.domain.product.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import fastcampus.ecommerce.api.domain.order.IllegalOrderStateException;
import fastcampus.ecommerce.api.domain.order.Order;
import fastcampus.ecommerce.api.domain.order.OrderStatus;
import fastcampus.ecommerce.api.domain.payment.PaymentMethod;
import fastcampus.ecommerce.api.domain.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.createOrder(1L);
        order.addOrderItem("PROD001", 2, 100);
        order.initPayment(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void testCompletePaymentSuccess() {
        order.completePayment(true);

        assertAll(
            () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING),
            () -> assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED),
            () -> assertThat(order.isPaymentSuccess()).isTrue());
    }

    @Test
    void testCompletePaymentFailed() {
        order.completePayment(false);

        assertAll(
            () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING),
            () -> assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED),
            () -> assertThat(order.isPaymentSuccess()).isFalse());
    }

    @Test
    void testCompletePaymentException() {
        order.completePayment(false);

        assertThatThrownBy(() -> order.completePayment(true))
            .isInstanceOf(IllegalOrderStateException.class);
    }

    @Test
    void testCompleteOrderPaymentSuccess() {
        order.completePayment(true);
        order.completeOrder();

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void testCompleteOrderPaymentFail() {
        order.completePayment(false);

        assertThatThrownBy(() -> order.completeOrder())
            .isInstanceOf(IllegalOrderStateException.class);
    }

    @Test
    void testCompleteOrderPaymentException() {
        assertThatThrownBy(() -> order.completeOrder())
            .isInstanceOf(IllegalOrderStateException.class);
    }

    @Test
    void cancelOrderTest() {
        order.completePayment(true);
        order.cancel();
        assertAll(
            () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED),
            () -> assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED)
        );
    }

    @Test
    void orderCancelAfterCompleteTest() {
        order.completePayment(true);
        order.completeOrder();

        assertThatThrownBy(() -> order.cancel())
            .isInstanceOf(IllegalOrderStateException.class);
    }

}