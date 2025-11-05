package devdojo.schema.user;

import devdojo.schema.entity.DateAuditable;
import devdojo.schema.entity.Identifiable;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Interface representing a user with identifiable properties and audit information.
 */
public interface IUser extends Identifiable<UUID>, DateAuditable {
  /**
   * Returns the unique Supabase ID used to identify this object
   * in the Supabase database.
   *
   * @return a non-null {@link UUID} representing the object's ID in Supabase
   */
  @NotNull(message = "ID cannot be null.")
  UUID supabaseId();

  @NotNull(message = "Email cannot be null.")
  String supabaseEmail();

  /**
   * @return the display name of the user.
   */
  @NotNull(message = "DisplayName cannot be null.")
  String displayName();

  /**
   * @return the role assigned to the user.
   */
  @NotNull(message = "Role cannot be null.")
  UserRole role();

  /**
   * @return whether the user is active.
   */
  boolean active();

  boolean emailVerified();

  /**
   * @return the last login timestamp of the user.
   */
  @NotNull(message = "LastLogin cannot be null.")
  Date lastLogin();
}
