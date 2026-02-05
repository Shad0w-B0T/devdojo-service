package devdojo.logic.database.user;

import devdojo.logic.database.ExternalEntityRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

public interface UserRepository extends ExternalEntityRepository<UserEntity> {
  boolean existsByDisplayName(final String displayName);

  @NotNull
  Optional<UserEntity> findByAuthId(final String authId);
}
