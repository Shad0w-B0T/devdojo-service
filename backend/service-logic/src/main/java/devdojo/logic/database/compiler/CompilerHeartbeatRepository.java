package devdojo.logic.database.compiler;

import devdojo.logic.database.EntityRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompilerHeartbeatRepository extends EntityRepository<CompilerHeartbeatEntity> {

  @Query(value = """
    SELECT 
      to_timestamp(floor(extract(epoch from ch.created_at) / 300) * 300) AS bucket,
      AVG(ch.cpu_usage) AS avg_cpu,
      AVG(ch.memory_usage) AS avg_mem
    FROM devdojo.compiler_heartbeat ch
    WHERE ch.compiler_id = :compilerId
      AND ch.created_at > NOW() - INTERVAL '1 day'
    GROUP BY bucket
    ORDER BY bucket
    """, nativeQuery = true)
  List<Object[]> findAggregatedByCompilerId(@Param("compilerId") UUID compilerId);

  @NotNull
  Optional<CompilerHeartbeatEntity> findTopByCompilerEntity_CompilerIdOrderByCreatedAtDesc(UUID compilerId);

  void deleteByCreatedAtBefore(Date cutoff);

}
