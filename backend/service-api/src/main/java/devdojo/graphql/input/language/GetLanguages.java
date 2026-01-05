package devdojo.graphql.input.language;

import devdojo.logic.language.ILanguageService;

public record GetLanguages(boolean onlyActive) implements ILanguageService.GetLanguages {
}
