package devdojo.logic.security.jwt;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class AbstractRefreshTokenService implements IRefreshTokenService {

  private static final Duration REFRESH_TTL = Duration.ofDays(14);

  private static final String KEY_PREFIX = "refresh:";
  private static final String REVOKED_PREFIX = "refresh:revoked:";

  private final StringRedisTemplate redis;

  protected AbstractRefreshTokenService(final StringRedisTemplate redis) {
    this.redis = redis;
  }

  @Override
  public String issue(final String authId) {
    final String token = UUID.randomUUID().toString().replace("-", "");
    final String key = KEY_PREFIX + token;

    this.redis.opsForValue().set(key, authId, REFRESH_TTL);
    return token;
  }

  @Override
  public Optional<String> rotate(final String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return Optional.empty();
    }

    if (Boolean.TRUE.equals(this.redis.hasKey(REVOKED_PREFIX + refreshToken))) {
      return Optional.empty();
    }

    final String key = KEY_PREFIX + refreshToken;
    final String authId = this.redis.opsForValue().get(key);
    if (authId == null) {
      return Optional.empty();
    }

    this.redis.delete(key);
    this.redis.opsForValue().set(REVOKED_PREFIX + refreshToken, "1", REFRESH_TTL);

    return Optional.of(authId);
  }

  @Override
  public void revoke(final String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }
    this.redis.delete(KEY_PREFIX + refreshToken);
    this.redis.opsForValue().set(REVOKED_PREFIX + refreshToken, "1", REFRESH_TTL);
  }

}
