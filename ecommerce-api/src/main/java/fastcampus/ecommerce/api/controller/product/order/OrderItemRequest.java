package fastcampus.ecommerce.api.controller.product.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemRequest {

    private Integer quantity;
    private Integer unit;
    private String productId;

    public static OrderItemRequest of(Integer quantity, Integer unit, String productId) {
        return new OrderItemRequest(quantity, unit, productId);
    }

}
