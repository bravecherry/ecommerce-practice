package fastcampus.ecommerce.api.controller.product.order;

import fastcampus.ecommerce.api.domain.order.OrderItemCommand;
import fastcampus.ecommerce.api.domain.payment.PaymentMethod;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

    private Long customerId;
    private List<OrderItemRequest> orderItems;
    private PaymentMethod paymentMethod;

    public OrderRequest(Long customerId, List<OrderItemRequest> orderItems) {
        this.customerId = customerId;
        this.orderItems = orderItems;
        this.paymentMethod = PaymentMethod.PAYPAL;
    }

    public List<OrderItemRequest> getOrderItemCommands() {
        return orderItems.stream().map(
                        item -> OrderItemCommand.of(
                                item.getQuantity(), item.getUnit(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    public List<OrderItemCommand> toOrderItemCommands() {
        return orderItems.stream()
                .map(item -> new OrderItemCommand(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());
        ;
    }
}
