package devdojo.logic.cache.bucket4j.aspect;

import devdojo.logic.cache.bucket4j.IRateLimitService;
import devdojo.logic.cache.bucket4j.annotation.RateLimited;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
public abstract class AbstractRateLimitAspect {

  private final IRateLimitService rateLimitService;
  private final SpelExpressionParser parser = new SpelExpressionParser();

  protected AbstractRateLimitAspect(final IRateLimitService rateLimitService) {
    this.rateLimitService = rateLimitService;
  }

  @Around("@annotation(rateLimited)")
  public Object applyRateLimit(final ProceedingJoinPoint joinPoint,
                               final RateLimited rateLimited) throws Throwable {
    final String ratelimitName = rateLimited.bucket();
    final String bucketName = this.evaluateBucketName(ratelimitName, joinPoint);

    this.rateLimitService.applyRateLimit(ratelimitName, bucketName, rateLimited.totalTokens(),
      rateLimited.refillDurationMillis(), rateLimited.consumeTokens(), rateLimited.waitForToken());

    return joinPoint.proceed();
  }

  private final static int SPEL_START = 2;

  /**
   * Evaluates a Spring Expression Language (SpEL) expression used to dynamically resolve
   * the bucket name for rate limiting, based on the method arguments of the join point.
   *
   * <p>If the input string is a SpEL expression (enclosed in "#{...}"), it is parsed and
   * evaluated in the context of the method and its parameters. Otherwise, the string is
   * returned as-is.</p>
   *
   * @param spel      the raw bucket name string, possibly containing a SpEL expression
   * @param joinPoint the current AOP join point providing method and argument context
   * @return the resolved bucket name after evaluating the expression, or the raw input if not an expression
   */
  final String evaluateBucketName(final String spel, final ProceedingJoinPoint joinPoint) {
    String expression = spel.trim();
    if (expression.startsWith("#{") && expression.endsWith("}")) {
      expression = expression.substring(SPEL_START, expression.length() - 1);
    } else {
      return spel;
    }

    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();

    final EvaluationContext context = new MethodBasedEvaluationContext(
      null, method, joinPoint.getArgs(), new DefaultParameterNameDiscoverer()
    );

    return this.parser.parseExpression(expression).getValue(context, String.class);
  }
}
