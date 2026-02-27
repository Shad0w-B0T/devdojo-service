package devdojo.logic.security.ratelimit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public interface IRateLimitService {

  void applyRateLimit(
    @NotBlank(message = "RateLimit name must not be blank")
    String ratelimit,

    @NotBlank(message = "Bucket name must not be blank")
    String bucket,

    @Positive(message = "Total tokens must be greater than 0")
    long totalTokens,

    @Positive(message = "Refill duration must be greater than 0")
    long refillMillis,

    @Min(value = 1, message = "At least one token must be consumed")
    long consumeTokens,

    @Min(value = 0, message = "Wait duration cannot be negative")
    long waitMillis
  );
}
