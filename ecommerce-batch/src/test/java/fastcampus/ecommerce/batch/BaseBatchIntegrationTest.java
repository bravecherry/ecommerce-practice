package fastcampus.ecommerce.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Sql("/sql/schema.sql")
@SpringBatchTest
@SpringJUnitConfig(classes = {BatchApplication.class})
public abstract class BaseBatchIntegrationTest {

    // job launcher 실행 환경 제공
    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    protected JdbcTemplate jdbcTemplate;

    //datasource를 통해 jdbcTemplate을 만든다.
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected static void assertJobCompleted(JobExecution jobExecution) {
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }

}
