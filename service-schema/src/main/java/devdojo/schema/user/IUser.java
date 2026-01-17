package devdojo.schema.user;

import devdojo.schema.entity.DateAuditable;
import devdojo.schema.entity.Identifiable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Interface representing a user with identifiable properties and audit information.
 */
public interface IUser extends Identifiable<UUID>, DateAuditable {

  @NotNull(message = "ID cannot be null.")
  String authId();

  @NotNull(message = "Email cannot be null.")
  String email();

  /**
   * @return the display name of the user.
   */
  @NotNull(message = "DisplayName cannot be null.")
  String displayName();

  /**
   * @return the default ldap role assigned to the user.
   */
  @NotNull(message = "LDAP role cannot be null.")
  UserRole ldapRole();

  /**
   * @return the internal application assigned user role.
   */
  @Nullable
  UserRole applicationRole();

  /**
   * @return whether the user is active.
   */
  boolean active();

  /**
   * @return the last login timestamp of the user.
   */
  @NotNull(message = "LastLogin cannot be null.")
  Date lastLogin();
}
