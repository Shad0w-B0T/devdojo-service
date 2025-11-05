package devdojo.logic.database.language;

import devdojo.logic.database.EntityRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A repository for accessing {@link LanguageEntity} data.
 * <p>
 * Provides methods to find a language entry or check if one exists.
 */
public interface LanguageRepository extends EntityRepository<LanguageEntity> {

  boolean existsByLanguageName(@NotNull final String languageName);

  @NotNull
  Optional<LanguageEntity> findById(@NotNull(message = "ID cannot be null.") final UUID uuid);

  @NotNull
  List<LanguageEntity> findAllByActiveTrue();

  @NotNull
  Optional<LanguageEntity> findByToken(@NotNull(message = "Token cannot be null.") final String token);
}
