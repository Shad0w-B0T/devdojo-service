package devdojo.logic.language;

import devdojo.logic.database.language.LanguageRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;


@Service
public class LanguageService extends AbstractLanguageService {
  public LanguageService(@NotNull final LanguageRepository languageRepository) {
    super(languageRepository);
  }
}
