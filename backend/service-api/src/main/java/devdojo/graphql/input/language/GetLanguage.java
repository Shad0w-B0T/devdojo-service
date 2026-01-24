package devdojo.graphql.input.language;

import devdojo.logic.language.ILanguageService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetLanguage(@NotNull(message = "LanguageId cannot be null.") String languageId) implements ILanguageService.LanguageGet {
}
