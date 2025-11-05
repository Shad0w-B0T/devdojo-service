package devdojo.logic.database;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EntityRepository<ENTITY extends AbstractEntity>
  extends JpaRepository<ENTITY, UUID>, JpaSpecificationExecutor<ENTITY> {
}
