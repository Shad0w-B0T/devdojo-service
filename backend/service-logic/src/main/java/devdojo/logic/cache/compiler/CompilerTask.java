package devdojo.logic.cache.compiler;

import devdojo.schema.task.Test;
import jakarta.persistence.Id;
import java.io.*;
import java.util.List;
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

  private static final long TTL = 120L;

  public enum Mode { TASK, RAW }

  @Id
  private UUID id;

  private String methodCode;
  private boolean processed;

  private List<Test> tests;

  /**
   * Optional. If null => RAW mode.
   */
  private String taskId;
  private String languageId;

  private String userId;

  private Mode mode;

  @TimeToLive
  private Long ttl = TTL;

  public CompilerTask(final UUID id,
                      final boolean processed,
                      final String methodCode,
                      final List<Test> tests,
                      final String taskId,
                      final String languageId,
                      final String userId) {
    this.id = id;
    this.processed = processed;
    this.methodCode = methodCode;
    this.tests = tests;

    this.taskId = taskId;
    this.languageId = languageId;
    this.userId = userId;
    this.mode = (taskId == null) ? Mode.RAW : Mode.TASK;
  }
}
