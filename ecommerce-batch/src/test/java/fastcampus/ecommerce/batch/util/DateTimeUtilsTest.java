package fastcampus.ecommerce.batch.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateTimeUtilsTest {

    @Test
    void testToLocalDate() {
        String date = "2026-01-15";
        LocalDate result = DateTimeUtils.toLocalDate(date);
        assertThat(result).isEqualTo(LocalDate.of(2026, 1, 15));
    }

    @Test
    void testToLocalDateTime() {
        String date = "2026-01-15 15:20:25.404";
        LocalDateTime result = DateTimeUtils.toLocalDateTime(date);
        assertThat(result).isEqualTo(LocalDateTime.of(2026, 1, 15,
                15, 20, 25, 404000000));
    }

    @Test
    void testLocalDateTimeToString() {
        LocalDateTime localDateTime = LocalDateTime.of(2026, 1, 25, 16, 56, 27, 0);
        String result = DateTimeUtils.toString(localDateTime);
        assertThat(result).isEqualTo("2026-01-25 16:56:27.000");
    }

}