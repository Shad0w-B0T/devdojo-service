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

  public void blacklist(String token) {
    String jti = jwtService.extractJti(token);
    Duration ttl = jwtService.getRemainingValidityDuration(token);
    redis.opsForValue().set(
      "jwt:blacklist:" + jti,
      "1",
      ttl
    );
  }

  public boolean isBlacklisted(String jti) {
    return Boolean.TRUE.equals(redis.hasKey("jwt:blacklist:" + jti));
  }
}
