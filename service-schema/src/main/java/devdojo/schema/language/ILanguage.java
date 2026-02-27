package devdojo.schema.language;

import devdojo.schema.entity.Activatable;
import devdojo.schema.entity.DateAuditable;
import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Interface representing a language with identifiable properties and audit information.
 */
public interface ILanguage extends Identifiable<UUID>, DateAuditable, Activatable {

  /**
   * @return the language name.
   */
  @NotNull(message = "LanguageName cannot be null.")
  String languageName();

  /**
   * @return the description of the language.
   */
  @NotNull(message = "Description cannot be null.")
  String description();
}
