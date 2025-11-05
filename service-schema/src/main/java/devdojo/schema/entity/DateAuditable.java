package devdojo.schema.entity;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * Interface for entities that are auditable by creation and update dates.
 */
public interface DateAuditable {
  /**
   * Gets the date and time when the entity was created.
   *
   * @return the creation date and time
   */
  @NotNull(message = "Created at cannot be null.")
  Date createdAt();

  /**
   * Gets the date and time when the entity was last updated.
   *
   * @return the last update date and time
   */
  @NotNull(message = "Updated at cannot be null.")
  Date updatedAt();
}
