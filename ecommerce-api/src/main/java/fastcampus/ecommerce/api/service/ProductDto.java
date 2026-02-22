package fastcampus.ecommerce.api.service;

import fastcampus.ecommerce.api.domain.product.Product;
import fastcampus.ecommerce.api.domain.product.ProductStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDto {

    private String productId;
    private Long sellerId;
    private String category;
    private String productName;
    private LocalDate salesStartDate;
    private LocalDate salesEndDate;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String brand;
    private String manufacturer;
    private int salesPrice;
    private int stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductDto of(String productId, Long sellerId, String category,
            String productName,
            LocalDate salesStartDate, LocalDate salesEndDate, ProductStatus productStatus,
            String brand, String manufacturer, int salesPrice, int stockQuantity,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new ProductDto(productId, sellerId, category, productName, salesStartDate,
                salesEndDate,
                productStatus, brand, manufacturer, salesPrice, stockQuantity, createdAt,
                updatedAt);
    }

    // 이 대신에 간단하게 mapper를 사용할수 있지만 mapper를 사용하여 내부 동작을 다시 확인하고 디버깅하는 것보다, 이게 더 직관적이어서 이 방향으로 결정
    public static ProductDto from(Product product) {
        return new ProductDto(
                product.getProductId(), product.getSellerId(), product.getProductName(),
                product.getProductName(), product.getSalesStartDate(), product.getSalesEndDate(),
                product.getProductStatus(), product.getBrand(), product.getManufacturer(),
                product.getSalesPrice(), product.getStockQuantity(), product.getCreatedAt(),
                product.getUpdatedAt());
    }

}
