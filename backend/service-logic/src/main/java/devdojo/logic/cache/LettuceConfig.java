package devdojo.logic.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class for initializing Lettuce Redis client using Spring's default Redis properties.
 * <p>
 * The property keys follow Spring's internal Redis configuration structure
 * (i.e., {@code spring.data.redis.*}) to ensure consistency and ease of integration.
 */
@Validated
@Configuration
public class LettuceConfig extends AbstractLettuceConfig {
  /**
   * Constructs the Redis configuration using Spring-standard Redis properties.
   *
   * @param redisHost     Redis host (from {@code spring.data.redis.host})
   * @param redisPort     Redis port (from {@code spring.data.redis.port})
   * @param redisPassword Redis password (from {@code spring.data.redis.password})
   * @param redisDatabase Redis database index (from {@code spring.data.redis.database})
   */
  public LettuceConfig(@Value("${spring.data.redis.host}") final String redisHost,
                       @Value("${spring.data.redis.port}") final int redisPort,
                       @Value("${spring.data.redis.password}") final String redisPassword,
                       @Value("${spring.data.redis.database}") final int redisDatabase) {
    super(redisHost, redisPort, redisPassword, redisDatabase);
  }
}
