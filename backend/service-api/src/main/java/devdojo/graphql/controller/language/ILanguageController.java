package devdojo.graphql.controller.language;

import devdojo.logic.database.language.LanguageEntity;
import jakarta.validation.constraints.NotNull;

public interface ILanguageController {

  // ────── Schema ──────

  //Language
  @NotNull
  String id(@NotNull final LanguageEntity languageEntity);

  @NotNull
  String languageName(@NotNull final LanguageEntity languageEntity);

  @NotNull
  String description(@NotNull final LanguageEntity languageEntity);

  boolean active(@NotNull final LanguageEntity languageEntity);

}
