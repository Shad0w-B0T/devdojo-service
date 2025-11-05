package devdojo.graphql.input.language;

import devdojo.logic.language.ILanguageService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateLanguage(@NotNull(message = "LanguageId cannot be null.") UUID languageId,
                             @NotNull(message = "LanguageName cannot be null.") String languageName,
                             @NotNull(message = "Description cannot be null.") String description,
                             @NotNull(message = "Active cannot be null.") boolean active)
  implements ILanguageService.LanguageUpdate {
}

