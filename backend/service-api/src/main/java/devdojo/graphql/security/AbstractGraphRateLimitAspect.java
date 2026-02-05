package devdojo.graphql.security;

import devdojo.logic.cache.bucket4j.IRateLimitService;
import devdojo.logic.security.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

public abstract class AbstractGraphRateLimitAspect implements IGraphRateLimitAspect{

  private final static long SECOND = 1000;
  private final IRateLimitService rateLimitService;
  private final int tokensPerSecond;

  protected AbstractGraphRateLimitAspect(final IRateLimitService rateLimitService,
                                         final int tokensPerSecond) {
    this.rateLimitService = rateLimitService;
    this.tokensPerSecond = tokensPerSecond;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Around("@annotation(org.springframework.graphql.data.method.annotation.QueryMapping) ||" +
    " @annotation(org.springframework.graphql.data.method.annotation.MutationMapping)")
  public Object rateLimitAllGraphQL(final ProceedingJoinPoint joinPoint) throws Throwable {
    final String bucketSpel =
      "#{'rate-graphql-' + T(devdojo.logic.security.SecurityUtils).localThreadClient().userId()}";

    this.rateLimitService.applyRateLimit(
      "ratelimit-graphql",
      "ratelimit-graphql-%s".formatted(SecurityUtils.currentTokenDetails().userId().toString()),
      this.tokensPerSecond,
      SECOND,
      1,
      0);

    return joinPoint.proceed();
  }
}
