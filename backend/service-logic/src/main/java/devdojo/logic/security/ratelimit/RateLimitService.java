package devdojo.logic.security.ratelimit;

import io.lettuce.core.RedisClient;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService extends AbstractRateLimitService {

  public RateLimitService(final RedisClient redisClient) {
    super(redisClient);
  }
}
