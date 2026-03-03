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
    private String productId;
    private String unit;

    public static OrderItemRequest of(Integer quantity, String productId, String unit) {
        return new OrderItemRequest(quantity, productId, unit);
    }

}
