package devdojo.logic.database.language;

import devdojo.logic.database.AbstractEntity;
import devdojo.logic.database.AbstractExternalEntity;
import devdojo.schema.language.ILanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a language entity.
 * <p>
 * Each language is uniquely identified by a UUID and has a unique, non-null language name.
 * This entity is mapped to the "language" table in the database.
 * </p>
 *
 * <p>
 * Note: The field {@code languageName} must not be {@code null}. The constructor enforces this
 * with a runtime null check.
 * </p>
 *
 * @see ILanguage
 * @see AbstractEntity
 */
@Entity
@Table(name = "language")
@Getter
@Setter
@NoArgsConstructor
@Accessors(fluent = true)
public final class LanguageEntity extends AbstractExternalEntity implements ILanguage, Serializable {

  @NotNull(message = "Language name must not be null")
  @Column(name = "languageName", nullable = false, unique = true)
  private String languageName;

  @NotNull(message = "Description must not be null")
  @Column(name = "description", nullable = false)
  private String description;

  @Column(nullable = false)
  boolean active;

  public LanguageEntity(final String languageName,
                        final String description,
                        final boolean active) {
    this.languageName = languageName;
    this.description = description;
    this.active = active;
  }

  @Override
  protected String prefix() {
    return "lng";
  }
}
