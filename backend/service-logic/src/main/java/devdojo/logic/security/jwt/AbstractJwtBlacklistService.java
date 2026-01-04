package devdojo.logic.security.jwt;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class AbstractJwtBlacklistService implements IJwtBlacklistService {
  private final StringRedisTemplate redis;
  private final IJwtService jwtService;

  protected AbstractJwtBlacklistService(final StringRedisTemplate redis,
                                        final IJwtService jwtService) {
    this.redis = redis;
    this.jwtService = jwtService;
  }

  public void blacklist(final String token) {
    final String jti = jwtService.extractJti(token);
    final Duration ttl = jwtService.getRemainingValidityDuration(token);
    this.redis.opsForValue().set(
      "jwt:blacklist:" + jti,
      "1",
      ttl
    );
  }

  public boolean isBlacklisted(final String jti) {
    return Boolean.TRUE.equals(redis.hasKey("jwt:blacklist:" + jti));
  }
}
