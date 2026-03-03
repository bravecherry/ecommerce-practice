package fastcampus.ecommerce.api.service.product;

import fastcampus.ecommerce.api.domain.product.Product;
import fastcampus.ecommerce.api.domain.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    //Product 와 상관없는 곳에서 해당 함수가 호출될수 있기 떄문에 Product 도메인 로직에 대한 접근을 제한하기 위해 ProductResult 라는 DTO추가
    public ProductDto findProduct(String id) {
        Product product = findProductById(id);
        return ProductDto.from(product);
    }

    public Page<ProductDto> getAllResults(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDto::from);
    }

    @Transactional
    public void increaseStock(String productId, int stockQuantity) {
        Product product = findProductById(productId);
        product.increaseStock(stockQuantity);
        productRepository.save(product);
    }

    @Transactional
    public void decreaseStock(String productId, int stockQuantity) {
        Product product = findProductById(productId);
        product.decreaseStock(stockQuantity);
        productRepository.save(product);
    }

    private Product findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

}
