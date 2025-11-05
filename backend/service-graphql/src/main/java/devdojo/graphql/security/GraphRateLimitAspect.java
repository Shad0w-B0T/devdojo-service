package devdojo.graphql.security;

import devdojo.logic.cache.bucket4j.IRateLimitService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GraphRateLimitAspect extends AbstractGraphRateLimitAspect {

  protected GraphRateLimitAspect(final IRateLimitService rateLimitService,
                                 @Value("${application.security.ratelimit.graphql.tokens_per_second:25}")
                                 final int tokensPerSecond) {
    super(rateLimitService, tokensPerSecond);
  }
}
