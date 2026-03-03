package fastcampus.ecommerce.api.domain.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemCommand {

    private Integer quantity;
    private Integer unitPrice;
    private String productId;

    public static OrderItemCommand of(Integer quantity, Integer unitPrice, String productId) {
        return new OrderItemCommand(quantity, unitPrice, productId);
    }
    
}
