package fastcampus.ecommerce.api.service.product;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("not found: " + productId);
    }
}
