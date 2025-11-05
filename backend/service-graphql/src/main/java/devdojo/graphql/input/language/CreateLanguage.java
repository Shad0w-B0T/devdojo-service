package devdojo.graphql.input.language;

import devdojo.logic.language.ILanguageService;
import jakarta.validation.constraints.NotNull;

public record CreateLanguage(@NotNull(message = "LanguageName cannot be null.") String languageName,
                             @NotNull(message = "Description cannot be null.") String description,
                             @NotNull(message = "Active cannot be null.") boolean active)
  implements ILanguageService.LanguageCreate {
}

