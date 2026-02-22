package fastcampus.ecommerce.api.domain.product;

public class StockQuantityArithmeticException extends ArithmeticException {

    public StockQuantityArithmeticException() {
        super("no more stocks addable");
    }
}
