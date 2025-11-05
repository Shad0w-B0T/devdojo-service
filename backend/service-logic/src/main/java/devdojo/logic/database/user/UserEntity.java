package devdojo.logic.database.user;

import devdojo.logic.database.AbstractEntity;
import devdojo.schema.Constants;
import devdojo.schema.user.IUser;
import devdojo.schema.user.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Entity representing a user in the database.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public final class UserEntity extends AbstractEntity implements IUser {

  @NotNull(message = "Supabase ID must not be null")
  @Column(name = "supabaseId", nullable = false, updatable = false, unique = true)
  private UUID supabaseId;

  @Email
  @NotNull(message = "Supabase Email must not be null")
  @Size(max = Constants.EMAIL_LENGTH, message = "Supabase Email must not exceed {max} characters")
  @Column(name = "supabaseEmail", length = Constants.EMAIL_LENGTH, nullable = false)
  private String supabaseEmail;

  @NotNull(message = "Display name must not be null")
  @Column(name = "displayName", nullable = false, unique = true)
  private String displayName;

  @NotNull(message = "User role must not be null")
  @Column(name = "role", nullable = false)
  private UserRole role;

  @NotNull(message = "Active flag must not be null")
  @Column(name = "active", nullable = false)
  private boolean active;

  @NotNull(message = "Email verified flag must not be null")
  @Column(name = "emailVerified", nullable = false)
  private boolean emailVerified;

  @NotNull(message = "Last login must not be null")
  @Past(message = "Last login date must be in the past")
  @Column(name = "lastLogin", nullable = false)
  private Date lastLogin;

  /**
   * Creates a new UserEntity.
   *
   * @param supabaseId    the supabase id.
   * @param supabaseEmail the email
   * @param displayName   the name displayed in the leaderboard.
   * @param role          the role of the user.
   * @param active        whether the user is active or not.
   * @param emailVerified if the email if verified by aal from supabase
   * @param lastLogin     the last login timestamp.
   */
  public UserEntity(@NotNull final UUID supabaseId,
                    @NotNull final String supabaseEmail,
                    @NotNull final String displayName,
                    @NotNull final UserRole role,
                    @NotNull final Boolean active,
                    @NotNull final Boolean emailVerified,
                    @NotNull final Date lastLogin) {
    this.supabaseId = supabaseId;
    this.supabaseEmail = supabaseEmail;
    this.displayName = displayName;
    this.role = role;
    this.active = Boolean.TRUE.equals(active);
    this.emailVerified = Boolean.TRUE.equals(emailVerified);
    this.lastLogin = lastLogin;
  }
}
