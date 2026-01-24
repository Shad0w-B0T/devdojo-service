package devdojo.logic.database;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExternalEntityRepository<ENTITY extends AbstractExternalEntity> extends EntityRepository<ENTITY> {
  Optional<ENTITY> findByExternalId(final String externalId);

  boolean existsByExternalId(final String externalId);
}
