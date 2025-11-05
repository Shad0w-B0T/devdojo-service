package devdojo.schema.entity;

import jakarta.validation.constraints.NotNull;

/**
 * Represents an entity that has a unique identifier.
 *
 * @param <TYPE> the type of the identifier
 */
public interface Identifiable<TYPE> {
  /**
   * Returns the unique identifier of this entity.
   *
   * @return the unique identifier, never {@code null}
   */
  @NotNull(message = "ID cannot be null.")
  TYPE id();
}
