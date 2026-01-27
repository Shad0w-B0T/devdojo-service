package devdojo.logic.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.schema.user.UserRole;
import jakarta.annotation.Nullable;
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

  @NotNull(message = "Returned user must not be null")
  UserEntity getUser(@NotNull(message = "User get data must not be null")
                     @Valid final GetUser getUser);

  UserEntity getInternalUser(@NotNull final UUID id);

  @NotNull(message = "Returned user must not be null")
  UserEntity getUser(@NotNull(message = "User get data must not be null")
                     @Valid final GetUserByAuthId getUserByAuthId);

  default @NotNull UserEntity getUser(@NotNull final String id) {
    return this.getUser(new GetUser(id));
  }

  /**
   * Creates a new user.
   *
   * @param createUser user creation data
   * @return the created user
   */
  @NotNull(message = "Created user must not be null")
  UserEntity createUser(@NotNull(message = "User create data must not be null")
                        @Valid final IUserService.CreateUser createUser);

  /**
   * Updates an existing user.
   *
   * @param updateUser user update data
   * @return the updated user
   */
  @NotNull(message = "Updated user must not be null")
  UserEntity updateUser(@NotNull(message = "User update data must not be null")
                        @Valid final IUserService.UpdateUser updateUser);

  @NotNull(message = "Updated user must not be null")
  UserEntity updateUser(@NotNull(message = "User role update data must not be null")
                        @Valid final IUserService.UpdateUserRole updateUserRole);

  @NotNull(message = "Updated user must not be null")
  UserEntity updateUser(@NotNull(message = "User role update data must not be null")
                        @Valid final IUserService.UpdateUserProfileVisibility updateUserRole);

  /**
   * Deletes a user by ID.
   *
   * @param deleteUser contains the user ID
   * @return the deleted user
   */
  @NotNull(message = "Deleted user must not be null")
  UserEntity deleteUser(@NotNull(message = "User delete data must not be null")
                        @Valid final IUserService.DeleteUser deleteUser);

  record GetUser(@NotNull(message = "User ID must not be null")
                 String userId) {

  }

  record GetUserByAuthId(@NotNull(message = "Auth ID must not be null")
                         String authId) {

  }

  record CreateUser(
    @NotNull(message = "Auth ID must not be null")
    String authId,

    @NotNull(message = "Email must not be null")
    String email,

    @NotNull(message = "Display name must not be null")
    String displayName,

    @NotNull(message = "LDAP role must not be null")
    UserRole ldapRole,

    @NotNull(message = "Application role must not be null")
    UserRole applicationRole,

    boolean active
  ) {
  }

  record UpdateUser(
    @NotNull(message = "User ID must not be null")
    String userId,

    @NotNull(message = "Email must not be null")
    String email,

    @NotNull(message = "Display name must not be null")
    String displayName,

    @Nullable
    UserRole applicationRole,

    boolean active,

    @NotNull(message = "Last login date must not be null")
    Date lastLogin,

    boolean profilePublic
  ) {
  }

  record UpdateUserRole(
    @NotNull(message = "User ID must not be null")
    String userId,

    @Nullable
    UserRole role
  ) {
  }

  record UpdateUserProfileVisibility(
    @NotNull(message = "User ID must not be null")
    String userId,

    boolean profilePublic
  ) {
  }

  record DeleteUser(
    @NotNull(message = "User ID must not be null")
    String userId
  ) {
  }
}


