package fastcampus.ecommerce.api.domain.product;

import lombok.Getter;

@Getter
public enum ProductStatus {
    AVAILABLE("판매 중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매 종료"),
    ;
    final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}
