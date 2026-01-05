package devdojo.logic.security.jwt;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService extends AbstractRefreshTokenService {

  protected RefreshTokenService(final StringRedisTemplate redis) {
    super(redis);
  }
}
