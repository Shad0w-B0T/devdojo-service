package devdojo.graphql.controller.language;

import devdojo.graphql.input.language.*;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.language.LanguageService;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.language.ILanguage;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Controller for handling GraphQL queries and mutations related to languages.
 * <p>
 * This controller exposes methods for fetching, creating, and deleting language entities.
 * It interacts with the {@link LanguageRepository} to perform the necessary database operations.
 * </p>
 *
 * @see LanguageRepository
 * @see LanguageEntity
 * @see ILanguage
 * @see AlreadyExistsException
 */
@Controller
public class LanguageController extends AbstractLanguageController {

  /**
   * The {@link LanguageRepository} used for database operations related to languages.
   */
  private final @NotNull(message = "LanguageService cannot be null.") LanguageService languageService;

  public LanguageController(final LanguageService languageService) {
    Objects.requireNonNull(languageService, "LanguageService is null, but is required to work with an language.");
    this.languageService = languageService;
  }

  /**
   * Retrieves all languages from the database.
   *
   * @param getLanguages graphql parameters to query for languages
   * @return a list of {@link ILanguage} representing all languages stored in the database.
   */
  @QueryMapping("languages")
  public List<ILanguage> languages(@Argument("input") final GetLanguages getLanguages) {
    return this.languageService
      .getLanguages(getLanguages)
      .stream()
      .map(entity -> (ILanguage) entity)
      .collect(Collectors.toList());
  }

  /**
   * Retrieves a language by its ID.
   *
   * @param getLanguage contains the id of the language.
   * @return the {@link ILanguage} corresponding to the provided {@code id}.
   */
  @QueryMapping("language")
  public ILanguage getLanguage(@Argument("input") final GetLanguage getLanguage) {
    return this.languageService.getLanguage(getLanguage);
  }

  /**
   * Creates a new language in the database.
   *
   * @param createLanguage contains all the data to create a language.
   * @return the created {@link ILanguage} entity.
   */
  @MutationMapping("createLanguage")
  public ILanguage createLanguage(@Argument("input") final CreateLanguage createLanguage) {
    return this.languageService.createLanguage(createLanguage);
  }


  /**
   * Updates the name of an existing language entity identified by its ID.
   *
   * @param updateLanguage contains all the data to update a language.
   * @return the updated {@link ILanguage} entity.
   */
  @MutationMapping("updateLanguage")
  public ILanguage updateLanguage(@Argument("input") final UpdateLanguage updateLanguage) {
    return this.languageService.updateLanguage(updateLanguage);
  }

  /**
   * Deletes a language by its ID.
   *
   * @param deleteLanguage contains all the data to delete a language.
   * @return {@code true} if the language was successfully deleted, {@code false} otherwise.
   */
  @MutationMapping("deleteLanguage")
  public ILanguage deleteLanguage(@Argument("input") final DeleteLanguage deleteLanguage) {
    return this.languageService.deleteLanguage(deleteLanguage);
  }
}
