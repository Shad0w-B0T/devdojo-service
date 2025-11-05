package devdojo.graphql.controller.language;

import devdojo.logic.database.language.LanguageEntity;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

public abstract class AbstractLanguageController implements ILanguageController {
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
