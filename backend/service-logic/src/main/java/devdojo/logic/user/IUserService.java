package devdojo.logic.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.schema.user.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * User service interface for basic CRUD operations.
 */
public interface IUserService {

  boolean isAdminNeeded();

  @NotNull(message = "Returned users must not be null")
  List<UserEntity> getUsers();

  /**
   * Gets a user by ID.
   *
   * @param userGet contains the user ID
   * @return the found user
   */
  @NotNull(message = "Returned user must not be null")
  UserEntity getUser(@NotNull(message = "User get data must not be null")
                     @Valid final UserGet userGet);

  default @NotNull UserEntity getUser(@NotNull final UUID id) {
    return this.getUser(new UserGet() {
      @Override
      public UUID userId() {
        return id;
      }

      @Override
      public UUID supabaseId() {
        return null;
      }
    });
  }

  /**
   * Creates a new user.
   *
   * @param userCreate user creation data
   * @return the created user
   */
  @NotNull(message = "Created user must not be null")
  UserEntity createUser(@NotNull(message = "User create data must not be null")
                        @Valid final UserCreate userCreate);

  /**
   * Updates an existing user.
   *
   * @param userUpdate user update data
   * @return the updated user
   */
  @NotNull(message = "Updated user must not be null")
  UserEntity updateUser(@NotNull(message = "User update data must not be null")
                        @Valid final UserUpdate userUpdate);

  @NotNull(message = "Updated user must not be null")
  UserEntity updateUser(@NotNull(message = "User displayname update data must not be null")
                        @Valid final UserDisplayNameUpdate userDisplayNameUpdate);

  @NotNull(message = "Updated user must not be null")
  UserEntity updateUser(@NotNull(message = "User role update data must not be null")
                        @Valid final UserRoleUpdate userRoleUpdate);

  /**
   * Deletes a user by ID.
   *
   * @param userDelete contains the user ID
   * @return the deleted user
   */
  @NotNull(message = "Deleted user must not be null")
  UserEntity deleteUser(@NotNull(message = "User delete data must not be null")
                        @Valid final UserDelete userDelete);

  interface UserGet {
    @NotNull(message = "User ID must not be null")
    UUID userId();

    @NotNull(message = "Supabase ID must not be null")
    UUID supabaseId();
  }

  interface UserCreate {
    @NotNull(message = "Supabase ID must not be null")
    UUID supabaseId();

    @NotNull(message = "Supabase Email must not be null")
    String supabaseEmail();

    @NotNull(message = "Display name must not be null")
    String displayName();

    @NotNull(message = "User role must not be null")
    UserRole role();

    boolean active();

    boolean emailVerified();
  }

  interface UserUpdate {
    @NotNull(message = "User ID must not be null")
    UUID userId();

    @NotNull(message = "Supabase Email must not be null")
    String supabaseEmail();

    @NotNull(message = "Display name must not be null")
    String displayName();

    @NotNull(message = "User role must not be null")
    UserRole role();

    boolean active();

    boolean emailVerified();

    @NotNull(message = "Last login date must not be null")
    Date lastLogin();
  }

  interface UserDisplayNameUpdate {
    @NotNull(message = "User ID must not be null")
    UUID userId();

    @NotNull(message = "Display name must not be null")
    String displayName();
  }

  interface UserRoleUpdate {
    @NotNull(message = "User ID must not be null")
    UUID userId();

    @NotNull(message = "User role must not be null")
    UserRole role();
  }

  interface UserDelete {
    @NotNull(message = "User ID must not be null")
    UUID userId();
  }
}


