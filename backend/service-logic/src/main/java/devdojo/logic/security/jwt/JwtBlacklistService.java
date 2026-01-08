package devdojo.logic.security.jwt;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JwtBlacklistService extends AbstractJwtBlacklistService {

  protected JwtBlacklistService(final StringRedisTemplate redis,
                                final IJwtService jwtService) {
    super(redis, jwtService);
  }
}
