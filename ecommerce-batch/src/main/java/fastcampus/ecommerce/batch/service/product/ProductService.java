package fastcampus.ecommerce.batch.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final JdbcTemplate jdbcTemplate;

    public Long countProducts() {
        return jdbcTemplate.queryForObject("select count(*) from products", Long.class);
    }
}
