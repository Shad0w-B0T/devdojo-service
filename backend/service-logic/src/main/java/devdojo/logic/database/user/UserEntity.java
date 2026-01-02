package devdojo.logic.database.user;

import devdojo.logic.database.AbstractEntity;
import devdojo.schema.Constants;
import devdojo.schema.user.IUser;
import devdojo.schema.user.UserRole;
import jakarta.persistence.*;
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

  @NotNull(message = "Auth ID must not be null")
  @Column(name = "authId", nullable = false, updatable = false, unique = true)
  private String authId;

  @Email
  @NotNull(message = "Email must not be null")
  @Size(max = Constants.EMAIL_LENGTH, message = "Email must not exceed {max} characters")
  @Column(name = "email", length = Constants.EMAIL_LENGTH, nullable = false)
  private String email;

  @NotNull(message = "Display name must not be null")
  @Column(name = "displayName", nullable = false, unique = true)
  private String displayName;

  @NotNull(message = "User role must not be null")
  @Column(name = "role", nullable = false)
  private UserRole role;

  @NotNull(message = "Active flag must not be null")
  @Column(name = "active", nullable = false)
  private boolean active;

  @NotNull(message = "Last login must not be null")
  @Past(message = "Last login date must be in the past")
  @Column(name = "lastLogin", nullable = false)
  private Date lastLogin;

  @OneToOne(
    mappedBy = "user",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL,
    optional = false
  )
  private UserSettingsEntity settings;

  /**
   * Creates a new UserEntity.
   *
   * @param authId      the supabase id.
   * @param displayName the name displayed in the leaderboard.
   * @param role        the role of the user.
   * @param active      whether the user is active or not.
   * @param lastLogin   the last login timestamp.
   */
  public UserEntity(@NotNull final String authId,
                    @NotNull final String email,
                    @NotNull final String displayName,
                    @NotNull final UserRole role,
                    @NotNull final Boolean active,
                    @NotNull final Date lastLogin) {
    this.authId = authId;
    this.email = email;
    this.displayName = displayName;
    this.role = role;
    this.active = Boolean.TRUE.equals(active);
    this.lastLogin = lastLogin;
  }
}
