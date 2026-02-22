package fastcampus.ecommerce.api.service;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("not found: " + productId);
    }
}
