package fastcampus.ecommerce.api.service.order;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("order not found: " + orderId);
    }
}
