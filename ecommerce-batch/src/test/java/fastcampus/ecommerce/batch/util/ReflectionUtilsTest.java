package fastcampus.ecommerce.batch.util;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReflectionUtilsTest {

    @Test
    void getFieldNames() {
        List<String> fieldNames = ReflectionUtils.getFieldNames(FieldTest.class);

        assertThat(fieldNames).hasSize(2)
                .containsExactly("stringField", "intField")
                .doesNotContain("CONSTANT");
    }

    private static class FieldTest {
        private String stringField;
        private int intField;
        public static final String CONSTANT = "CONSTANT";
    }

}