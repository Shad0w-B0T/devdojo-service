package devdojo.graphql.controller.language;

import devdojo.graphql.input.language.CreateLanguage;
import devdojo.graphql.input.language.DeleteLanguage;
import devdojo.graphql.input.language.GetLanguage;
import devdojo.graphql.input.language.GetLanguages;
import devdojo.graphql.input.language.UpdateLanguage;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.schema.language.ILanguage;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

public abstract class AbstractLanguageController implements ILanguageController {


  /**
   * The {@link LanguageRepository} used for database operations related to languages.
   */
  private final @NotNull(message = "LanguageService cannot be null.") ILanguageService languageService;

  public AbstractLanguageController(final ILanguageService languageService) {
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


  @SchemaMapping(typeName = "Language", value = "id")
  @Override
  public @NotNull UUID id(@NotNull final LanguageEntity languageEntity) {
    return languageEntity.id();
  }

  @SchemaMapping(typeName = "Language", value = "token")
  public @NotNull String token(@NotNull final LanguageEntity languageEntity) {
    return languageEntity.token();
  }

  @SchemaMapping(typeName = "Language", value = "languageName")
  @Override
  public @NotNull String languageName(@NotNull final LanguageEntity languageEntity) {
    return languageEntity.languageName();
  }

  @SchemaMapping(typeName = "Language", value = "description")
  @Override
  public @NotNull String description(@NotNull final LanguageEntity languageEntity) {
    return languageEntity.description();
  }

  @SchemaMapping(typeName = "Language", value = "active")
  @Override
  public @NotNull boolean active(@NotNull final LanguageEntity languageEntity) {
    return languageEntity.active();
  }
}
