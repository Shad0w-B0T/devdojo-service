package devdojo.logic.database.user;

import devdojo.logic.database.EntityRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends EntityRepository<UserEntity> {
  boolean existsByDisplayName(final String displayName);

  @NotNull Optional<UserEntity> findByAuthId(final String authId);
}
