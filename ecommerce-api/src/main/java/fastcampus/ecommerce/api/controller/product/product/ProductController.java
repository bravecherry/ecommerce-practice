package fastcampus.ecommerce.api.controller.product.product;

import fastcampus.ecommerce.api.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ProductResDto findById(@PathVariable String productId) {
        return ProductResDto.from(productService.findProduct(productId));
    }

    @GetMapping
    public Page<ProductResDto> findById(@RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "productId,asc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sort[1].equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC,
                        sort[0]));
        return productService.getAllResults(pageable).map(ProductResDto::from);
    }

}
