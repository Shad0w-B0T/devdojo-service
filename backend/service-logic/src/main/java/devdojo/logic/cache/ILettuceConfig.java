package devdojo.logic.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration interface for managing Lettuce Redis clients and connections.
 * <p>
 * Implementations of this interface provide access to a configured {@link RedisClient}
 * and its associated {@link StatefulRedisConnection} for interacting with a Redis datastore.
 *
 * <p>
 * This interface is typically used for setting up Redis integration in applications
 * that utilize the Lettuce Redis client library.
 *
 * @see RedisClient
 * @see StatefulRedisConnection
 */
public interface ILettuceConfig {
  /**
   * Creates or retrieves a non-null {@link RedisClient} instance.
   * <p>
   * The Redis client is responsible for creating connections to a Redis server or cluster.
   *
   * @return a configured and non-null {@link RedisClient} instance
   */
  @NotNull(message = "RedisClient must not be null")
  RedisClient redisClient();

  /**
   * Provides a {@link StatefulRedisConnection} using the given {@link RedisClient}.
   * <p>
   * This connection is used to perform Redis commands in a thread-safe manner.
   *
   * @param redisClient the Redis client used to establish the connection; must not be {@code null}
   * @return a non-null stateful connection to Redis
   */
  @NotNull(message = "StatefulRedisConnection must not be null")
  StatefulRedisConnection<String, String> redisConnection(
    @NotNull(message = "RedisClient parameter must not be null") final RedisClient redisClient
  );
}
