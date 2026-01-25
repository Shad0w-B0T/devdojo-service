package devdojo.logic.cache.compiler;

import jakarta.persistence.Id;
import java.io.*;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@NoArgsConstructor
@Getter
@Accessors(fluent = true)
@RedisHash("CompilerTask")
public final class CompilerTask implements Serializable {

  private final static long TTL = 120L;

  @Id
  private UUID id;

  private String methodCode;

  private boolean processed;

  private String taskId;
  private String userId;

  @TimeToLive
  private Long ttl = TTL;

  public CompilerTask(final UUID id,
                      final boolean processed,
                      final String methodCode,
                      final String taskId,
                      final String userId) {
    this.id = id;
    this.processed = processed;
    this.methodCode = methodCode;
    this.taskId = taskId;
    this.userId = userId;
  }
}
