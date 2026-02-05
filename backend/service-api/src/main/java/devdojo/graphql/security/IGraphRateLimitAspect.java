package devdojo.graphql.security;

import org.aspectj.lang.ProceedingJoinPoint;

public interface IGraphRateLimitAspect {

  /**
   * Rate limits GraphQL requests by applying token-bucket-based rate limiting.
   * This method intercepts GraphQL queries or mutations at runtime to enforce
   * rate limits per user session.
   *
   * @param joinPoint The join point representing the context of the intercepted
   *                  GraphQL method execution, including method details and arguments.
   *
   * @return The result of executing the intercepted GraphQL query or mutation
   *         after ensuring applicable rate-limit policies.
   *
   * @throws Throwable If an exception occurs while executing the intercepted method
   *                   or if rate limit rules cannot be enforced properly.
   */
  Object rateLimitAllGraphQL(final ProceedingJoinPoint joinPoint) throws Throwable;
}
