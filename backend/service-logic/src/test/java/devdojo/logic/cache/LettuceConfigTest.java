package devdojo.logic.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("LettuceConfig Unit Tests")
final class LettuceConfigTest {

  private final String redisHost = "localhost";
  private final int redisPort = 6379;
  private final String redisPassword = "testpass";
  private final int redisDatabase = 1;

  private LettuceConfig config;

  @BeforeEach
  void setUp() {
    this.config = new LettuceConfig(redisHost, redisPort, redisPassword, redisDatabase);
  }

  @Test
  @DisplayName("should connect to Redis and return connection")
  void shouldConnectToRedis() {
    final RedisClient redisClient = Mockito.mock(RedisClient.class);
    final StatefulRedisConnection<String, String> mockConnection = mock(StatefulRedisConnection.class);

    when(redisClient.connect()).thenReturn(mockConnection);

    final StatefulRedisConnection<String, String> result = this.config.redisConnection(redisClient);

    assertNotNull(result);
    verify(redisClient, times(1)).connect();
  }
}
