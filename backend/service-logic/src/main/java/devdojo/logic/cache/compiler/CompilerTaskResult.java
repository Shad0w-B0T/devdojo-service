package devdojo.logic.cache.compiler;

import devdojo.schema.code.TestRun;
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
@RedisHash("CompilerTaskResult")
public class CompilerTaskResult implements Serializable {

  private final static long TTL = 360L;

  @Id
  private UUID id;

  private String testRunJson;
  private UUID userId;

  @TimeToLive
  private Long ttl = TTL;

  public CompilerTaskResult(final UUID id,
                            final UUID userId,
                            final String testRunJson) {
    this.userId = userId;
    this.id = id;
    this.testRunJson = testRunJson;
  }
}
