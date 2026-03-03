package fastcampus.ecommerce.api.domain.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemResponse {

    private Long orderItemId;
    private Integer quantity;
    private Integer unitPrice;
    private String productId;

    public static OrderItemResponse of(OrderItemResult result) {
        return new OrderItemResponse(result.getOrderItemId(), result.getQuantity(),
                result.getUnitPrice(), result.getProductId());
    }

}
