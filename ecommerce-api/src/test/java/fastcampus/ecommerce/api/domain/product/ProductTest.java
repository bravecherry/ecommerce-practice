package fastcampus.ecommerce.api.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        product = Product.of("p1", 1L, "Electronics", "TestProduct", now.toLocalDate(),
                now.toLocalDate(), ProductStatus.AVAILABLE, "TestBrand", "TestManufacturer", 1000,
                100, now, now);
    }

    @Test
    void increaseStockTest() {
        product.increaseStock(50);
        assertThat(product.getStockQuantity()).isEqualTo(150);
    }

    @Test
    void increaseStockNegativeTest() {
        assertThatThrownBy(() -> product.increaseStock(Integer.MAX_VALUE)).isInstanceOf(
                StockQuantityArithmeticException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -1, 0})
    void increaseStockPositiveTest(int notPositiveQuantity) {
        assertThatThrownBy(() -> product.increaseStock(notPositiveQuantity)).isInstanceOf(
                InvalidStockQuantityException.class);
    }

    @Test
    void decreaseStockTest() {
        product.decreaseStock(50);
        assertThat(product.getStockQuantity()).isEqualTo(50);
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -1, 0})
    void decreaseStockPositiveTest(int notPositiveQuantity) {
        assertThatThrownBy(() -> product.decreaseStock(notPositiveQuantity)).isInstanceOf(
                InvalidStockQuantityException.class);
    }

    @Test
    void decreaseStockWithInsufficientTest() {
        assertThatThrownBy(() -> product.decreaseStock(101)).isInstanceOf(
                InsufficientStockQuantityException.class);
    }

}