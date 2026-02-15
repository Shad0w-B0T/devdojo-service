package devdojo.logic.websocket.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Redis-based implementation of {@link IPresenceService}.
 * <p>
 * Uses a Redis Set to maintain a cluster-wide list of
 * currently connected WebSocket users.
 */
@Slf4j
@Validated
@Service
public class PresenceService implements IPresenceService {

  private static final String KEY = "active:websocket:users";

  private final StringRedisTemplate redis;

  public PresenceService(
    @NotNull(message = "StringRedisTemplate must not be null") final StringRedisTemplate redis
  ) {
    this.redis = redis;
  }

  @Override
  public void userConnected(@NotBlank final String userId) {
    this.redis.opsForSet().add(KEY, userId);
    log.debug("Presence: user connected '{}'", userId);
  }

  @Override
  public void userDisconnected(@NotBlank final String userId) {
    this.redis.opsForSet().remove(KEY, userId);
    log.debug("Presence: user disconnected '{}'", userId);
  }

  @Override
  public long getActiveUserCount() {
    return Optional
      .ofNullable(this.redis.opsForSet().size(KEY))
      .orElse(0L);
  }
}
