package devdojo.logic.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.validation.annotation.Validated;

@EnableCaching
@Validated
@Slf4j
@EnableRedisRepositories
public abstract class AbstractLettuceConfig implements ILettuceConfig {

  private final String redisHost;
  private final int redisPort;
  private final String redisPassword;
  private final int redisDatabase;

  protected AbstractLettuceConfig(
    @NotNull(message = "Redis host must not be null") final String redisHost,
    final int redisPort,
    @NotNull(message = "Redis password must not be null") final String redisPassword,
    final int redisDatabase
  ) {
    this.redisHost = redisHost;
    this.redisPort = redisPort;
    this.redisPassword = redisPassword;
    this.redisDatabase = redisDatabase;
  }

  /**
   * {@inheritDoc}
   */
  @Bean
  @Override
  public RedisClient redisClient() {
    final String redisUrl =
      String.format("redis://:%s@%s:%d/%d", this.redisPassword, this.redisHost, this.redisPort, this.redisDatabase);

    final RedisClient redisClient = RedisClient.create(redisUrl);
    log.info("Created to redis client for {}:{}, ready for connections.", this.redisHost, this.redisPort);
    return redisClient;
  }

  /**
   * {@inheritDoc}
   */
  @Bean
  @Override
  public StatefulRedisConnection<String, String> redisConnection(final RedisClient redisClient) {
    final StatefulRedisConnection<String, String> connection = redisClient.connect();
    log.info("Connected to redis instance {}:{}.", this.redisHost, this.redisPort);
    return connection;
  }
}
