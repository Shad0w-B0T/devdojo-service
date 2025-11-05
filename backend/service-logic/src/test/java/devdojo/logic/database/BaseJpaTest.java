package devdojo.logic.database;


import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;


@DataJpaTest
@Testcontainers
@ActiveProfiles("dev")
public abstract class BaseJpaTest {

  private static final String POSTGRESQL_IMAGE = "postgres:latest";

  private final static PostgreSQLContainer<?> PSQL = new PostgreSQLContainer<>(POSTGRESQL_IMAGE)
    .withDatabaseName("test_db")
    .withUsername("test_user")
    .withPassword("test_pass")
    .waitingFor(Wait.forListeningPort());

  static {
    PSQL.start();
  }

  @DynamicPropertySource
  static void psqlProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", PSQL::getJdbcUrl);
    registry.add("spring.datasource.username", PSQL::getUsername);
    registry.add("spring.datasource.password", PSQL::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.flyway.enabled", () -> true);
  }

  @Test
  @DisplayName("Testing if the Container is running")
  void containerIsRunning() {
    assertTrue(PSQL.isRunning());
  }
}
