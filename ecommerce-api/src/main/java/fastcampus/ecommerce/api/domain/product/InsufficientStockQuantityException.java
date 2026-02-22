package fastcampus.ecommerce.api.domain.product;

public class InsufficientStockQuantityException extends RuntimeException {

    public InsufficientStockQuantityException() {
        super("insufficient stock quantity");
    }
}
