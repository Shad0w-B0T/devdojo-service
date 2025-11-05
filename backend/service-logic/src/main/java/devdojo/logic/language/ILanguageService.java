package devdojo.logic.language;

import devdojo.logic.database.language.LanguageEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ILanguageService {

  /**
   * Creates a new language entry in the system.
   *
   * @param languageCreate contains the data to create a language.
   * @return the created LanguageEntity.
   */
  @NotNull(message = "Created language must not be null")
  LanguageEntity createLanguage(@NotNull(message = "Language create data must not be null")
                                @Valid final LanguageCreate languageCreate);

  /**
   * Retrieves a language entity based on its unique identifier.
   *
   * @param languageGet contains the language id.
   * @return the found LanguageEntity, or {@code null} if no such entity exists.
   */
  @NotNull(message = "Returned language must not be null")
  LanguageEntity getLanguage(@NotNull(message = "Language get data must not be null")
                             @Valid final LanguageGet languageGet);

  /**
   * Retrieves all language entities from the repository.
   *
   * @param getLanguages parameter for language query
   * @return a non-null list of all {@link LanguageEntity} objects stored in the repository.
   */
  @NotNull(message = "List of languages must not be null")
  List<LanguageEntity> getLanguages(final GetLanguages getLanguages);

  /**
   * Updates an existing language entry in the system.
   *
   * @param languageUpdate contains the update data.
   * @return the updated LanguageEntity.
   */
  @NotNull(message = "Updated language must not be null")
  LanguageEntity updateLanguage(@NotNull(message = "Language update data must not be null")
                                @Valid final LanguageUpdate languageUpdate);

  /**
   * Deletes a language entity from the system using its unique identifier.
   *
   * @param languageDelete contains the language id.
   * @return the deleted LanguageEntity, or {@code null} if no entity was found.
   */
  @NotNull(message = "Deleted language must not be null")
  LanguageEntity deleteLanguage(@NotNull(message = "Language delete data must not be null")
                                @Valid final LanguageDelete languageDelete);

  interface GetLanguages {
    boolean onlyActive();
  }

  interface LanguageGet {
    @NotNull(message = "Language ID must not be null")
    UUID languageId();
  }

  interface LanguageCreate {
    @NotNull(message = "Language name must not be null")
    String languageName();

    @NotNull(message = "Description must not be null")
    String description();

    @NotNull(message = "Active must not be null")
    boolean active();
  }

  interface LanguageUpdate {
    @NotNull(message = "Language ID must not be null")
    UUID languageId();

    @NotNull(message = "Language name must not be null")
    String languageName();

    @NotNull(message = "Description must not be null")
    String description();

    @NotNull(message = "Active must not be null")
    boolean active();
  }

  interface LanguageDelete {
    @NotNull(message = "Language ID must not be null")
    UUID languageId();
  }
}

