package devdojo.logic.security.ratelimit;

import devdojo.logic.cache.bucket4j.exception.RateLimitExceededException;
import devdojo.logic.cache.bucket4j.exception.RateLimitTimeoutException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class AbstractRateLimitService implements IRateLimitService {

  private static final long EXPIRE_TIME_MINUTES = 30;
  private final Map<String, Supplier<BucketConfiguration>> configurationSupplierMap = new ConcurrentHashMap<>();
  private final ProxyManager<String> proxyManager;

  protected AbstractRateLimitService(final RedisClient redisClient) {
    Objects.requireNonNull(redisClient, "RedisClient must not be null");

    StatefulRedisConnection<String, byte[]> connection =
      redisClient.connect(new StringByteArrayCodec());

    this.proxyManager = Bucket4jLettuce
      .casBasedBuilder(connection)
      .expirationAfterWrite(
        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
          Duration.ofMinutes(EXPIRE_TIME_MINUTES)))
      .build();
  }

  /**
   * Applies rate limiting for a given bucket based on the provided configuration.
   * If {@code waitMillis} is greater than 0, the method will block up to that duration
   * to wait for a token; otherwise it fails fast if no token is available.
   *
   * @param ratelimit     logical key used to cache the bucket configuration
   * @param bucket        unique bucket name (e.g. per user/request type)
   * @param totalTokens   total tokens allowed in the bucket
   * @param refillMillis  duration in milliseconds to refill the bucket to its max
   * @param consumeTokens number of tokens to consume
   * @param waitMillis    how long to wait (in ms) if tokens are not available; 0 = non-blocking
   * @throws RateLimitExceededException if no token was available and waitMillis = 0
   * @throws RateLimitTimeoutException  if waiting was interrupted or timed out
   */
  @Override
  public void applyRateLimit(final String ratelimit,
                             final String bucket,
                             final long totalTokens,
                             final long refillMillis,
                             final long consumeTokens,
                             final long waitMillis) {
    final Supplier<BucketConfiguration> config = getConfigurationSupplier(ratelimit, totalTokens, refillMillis);

    if (waitMillis <= 0) {
      tryConsume(config, bucket, consumeTokens);
    } else {
      tryConsumeBlocking(config, bucket, consumeTokens, Duration.ofMillis(waitMillis));
    }
  }

  /**
   * Lazily retrieves or creates a bucket configuration based on the provided key and limits.
   *
   * @param ratelimit    logical name of the configuration
   * @param tokens       total capacity
   * @param refillMillis refill interval in milliseconds
   * @return a memoized supplier of {@link BucketConfiguration}
   */
  private Supplier<BucketConfiguration> getConfigurationSupplier(final String ratelimit,
                                                                 final long tokens,
                                                                 final long refillMillis) {
    return configurationSupplierMap.computeIfAbsent(ratelimit, __ ->
      () -> BucketConfiguration.builder()
        .addLimit(Bandwidth.builder()
          .capacity(tokens)
          .refillGreedy(tokens, Duration.ofMillis(refillMillis))
          .build())
        .build());
  }

  /**
   * Creates a new {@link Bucket} instance from the proxy manager.
   *
   * @param supplier the configuration supplier
   * @param name     the unique name of the bucket
   * @return the configured {@link Bucket}
   */
  private Bucket bucket(final Supplier<BucketConfiguration> supplier,
                        final String name) {
    return proxyManager.builder().build(name, supplier);
  }

  /**
   * Attempts to consume tokens immediately, without waiting.
   *
   * @param config     the configuration supplier
   * @param bucketName the name of the bucket
   * @param tokens     the number of tokens to consume
   * @throws RateLimitExceededException if no tokens are available
   */
  private void tryConsume(final Supplier<BucketConfiguration> config,
                          final String bucketName,
                          final long tokens) {
    if (!bucket(config, bucketName).tryConsume(tokens)) {
      throw new RateLimitExceededException("Rate limit for bucket " + bucketName + " exceeded");
    }
  }

  /**
   * Attempts to consume tokens, waiting up to the specified timeout if needed.
   *
   * @param config     the configuration supplier
   * @param bucketName the name of the bucket
   * @param tokens     the number of tokens to consume
   * @param timeout    the maximum wait duration
   * @throws RateLimitExceededException if the timeout expires without success
   * @throws RateLimitTimeoutException  if the thread is interrupted while waiting
   */
  private void tryConsumeBlocking(final Supplier<BucketConfiguration> config,
                                  final String bucketName,
                                  final long tokens,
                                  final Duration timeout) {
    try {
      if (!bucket(config, bucketName).asBlocking().tryConsume(tokens, timeout)) {
        throw new RateLimitExceededException("Rate limit for bucket " + bucketName + " exceeded");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RateLimitTimeoutException("Rate limit wait interrupted for bucket: " + bucketName);
    }
  }
}
