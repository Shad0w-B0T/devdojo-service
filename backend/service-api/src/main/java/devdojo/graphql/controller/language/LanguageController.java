package devdojo.graphql.controller.language;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.language.ILanguage;
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

  protected LanguageController(final ILanguageService languageService) {
    super(languageService);
  }
}
