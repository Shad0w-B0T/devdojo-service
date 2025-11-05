package devdojo.logic.database.compiler;

import devdojo.logic.database.EntityRepository;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompilerRepository extends EntityRepository<CompilerEntity> {

  @Modifying
  @Transactional
  @Query(value = """
    DELETE FROM devdojo.compiler
    WHERE id IN (
      SELECT compiler_id
      FROM devdojo.compiler_heartbeat
      GROUP BY compiler_id
      HAVING MAX(created_at) < :cutoff
    )
    """, nativeQuery = true)
  int deleteAllWithLastHeartbeatBefore(@Param("cutoff") Date cutoff);

  Optional<CompilerEntity> findByCompilerId(final UUID compilerId);

}
