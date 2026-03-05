package fastcampus.ecommerce.api.domain.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemCommand {

    private String productId;
    private Integer quantity;
}
