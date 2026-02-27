package devdojo.logic.database;

import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExternalEntityRepository<ENTITY extends AbstractExternalEntity> extends EntityRepository<ENTITY> {
  Optional<ENTITY> findByExternalId(final String externalId);

  boolean existsByExternalId(final String externalId);
}
