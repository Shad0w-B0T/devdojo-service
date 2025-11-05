package devdojo.graphql.input.language;

import devdojo.logic.language.ILanguageService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DeleteLanguage(@NotNull(message = "LanguageId cannot be null.") UUID languageId) implements ILanguageService.LanguageDelete {
}

