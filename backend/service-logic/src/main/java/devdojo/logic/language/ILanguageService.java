package devdojo.logic.language;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.schema.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface ILanguageService {

  /**
   * Creates a new language entry in the system.
   *
   * @param createLanguage contains the data to create a language.
   * @return the created LanguageEntity.
   */
  @NotNull(message = "Created language must not be null")
  LanguageEntity createLanguage(@NotNull(message = "Language create data must not be null")
                                @Valid final ILanguageService.CreateLanguage createLanguage);

  /**
   * Retrieves a language entity based on its unique identifier.
   *
   * @param getLanguage contains the language id.
   * @return the found LanguageEntity, or {@code null} if no such entity exists.
   */
  @NotNull(message = "Returned language must not be null")
  LanguageEntity getLanguage(@NotNull(message = "Language get data must not be null")
                             @Valid final ILanguageService.GetLanguage getLanguage);

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
   * @param updateLanguage contains the update data.
   * @return the updated LanguageEntity.
   */
  @NotNull(message = "Updated language must not be null")
  LanguageEntity updateLanguage(@NotNull(message = "Language update data must not be null")
                                @Valid final ILanguageService.UpdateLanguage updateLanguage);

  /**
   * Deletes a language entity from the system using its unique identifier.
   *
   * @param deleteLanguage contains the language id.
   * @return the deleted LanguageEntity, or {@code null} if no entity was found.
   */
  @NotNull(message = "Deleted language must not be null")
  LanguageEntity deleteLanguage(@NotNull(message = "Language delete data must not be null")
                                @Valid final ILanguageService.DeleteLanguage deleteLanguage);

  record GetLanguages(
    boolean onlyActive
  ) {
  }

  record GetLanguage(

    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    @NotNull(message = "Language ID must not be null")
    String languageId

  ) {
  }

  record CreateLanguage(

    @NotBlank(message = "Language name must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Language name must not exceed {max} characters")
    String languageName,

    @NotBlank(message = "Description must not be blank")
    @Size(max = Constants.DESCRIPTION_LENGTH, message = "Description must not exceed {max} characters")
    String description,

    boolean active

  ) {
  }

  record UpdateLanguage(

    @NotBlank(message = "Language ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    String languageId,

    @NotBlank(message = "Language name must not be blank")
    @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Language name must not exceed {max} characters")
    String languageName,

    @NotBlank(message = "Description must not be blank")
    @Size(max = Constants.DESCRIPTION_LENGTH, message = "Description must not exceed {max} characters")
    String description,

    boolean active

  ) {
  }

  record DeleteLanguage(

    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    @NotNull(message = "Language ID must not be null")
    String languageId

  ) {
  }
}

