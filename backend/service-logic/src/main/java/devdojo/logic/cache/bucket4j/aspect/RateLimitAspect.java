package devdojo.logic.cache.bucket4j.aspect;

import devdojo.logic.cache.bucket4j.IRateLimitService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect extends AbstractRateLimitAspect {
  public RateLimitAspect(final IRateLimitService rateLimitService) {
    super(rateLimitService);
  }
}
