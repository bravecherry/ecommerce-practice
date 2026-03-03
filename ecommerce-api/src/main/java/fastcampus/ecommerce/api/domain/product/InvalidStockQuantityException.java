package fastcampus.ecommerce.api.domain.product;

public class InvalidStockQuantityException extends RuntimeException {

    public InvalidStockQuantityException() {
        super("invalid stock quantity");
    }
}
