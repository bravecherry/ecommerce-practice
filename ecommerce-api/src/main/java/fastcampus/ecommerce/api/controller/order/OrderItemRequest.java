package fastcampus.ecommerce.api.controller.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemRequest {

    private String productId;
    private Integer quantity;

    public static OrderItemRequest of(String productId, Integer quantity) {
        return new OrderItemRequest(productId, quantity);
    }


}
