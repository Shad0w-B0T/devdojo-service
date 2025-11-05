package devdojo.logic.security.ratelimit;

import devdojo.logic.cache.bucket4j.exception.RateLimitExceededException;
import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("RateLimitService Integration Tests with Redis (Testcontainers)")
final class RateLimitServiceTest {

  private RateLimitService rateLimitService;
  private RedisClient redisClient;

  @Container
  static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:7-alpine")
    .withExposedPorts(6379);

  @BeforeAll
  void setUpRedisClient() {
    final String redisUri = String.format("redis://%s:%d",
      REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379));
    redisClient = RedisClient.create(redisUri);
    rateLimitService = new RateLimitService(redisClient);
  }

  @AfterAll
  void shutdownRedisClient() {
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  @Test
  @DisplayName("Should throw exception when rate limit is exceeded")
  void shouldThrowExceptionWhenRateLimitExceeded() {
    final String ratelimitKey = "test-limit";
    final String bucketKey = "user-123";
    final long totalTokens = 1;
    final long refillMillis = 60_000;
    final long consumeTokens = 1;
    final long waitMillis = 0;

    rateLimitService.applyRateLimit(
      ratelimitKey, bucketKey, totalTokens, refillMillis, consumeTokens, waitMillis
    );

    assertThrows(RateLimitExceededException.class, () -> {
      rateLimitService.applyRateLimit(
        ratelimitKey, bucketKey, totalTokens, refillMillis, consumeTokens, waitMillis
      );
    });
  }

  @Test
  @DisplayName("Should wait and succeed after token refill")
  void shouldWaitAndSucceedWhenTokensRefilled() {
    final String ratelimitKey = "wait-limit";
    final String bucketKey = "user-456";
    final long totalTokens = 1;
    final long refillMillis = 200;
    final long consumeTokens = 1;
    final long waitMillis = 500;

    rateLimitService.applyRateLimit(
      ratelimitKey, bucketKey, totalTokens, refillMillis, consumeTokens, waitMillis
    );

    rateLimitService.applyRateLimit(
      ratelimitKey, bucketKey, totalTokens, refillMillis, consumeTokens, waitMillis
    );
  }
}
