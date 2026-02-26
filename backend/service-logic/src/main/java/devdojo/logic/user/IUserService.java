package devdojo.logic.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.schema.Constants;
import devdojo.schema.user.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  record GetUser(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId

  ) {
  }


  record GetUserByAuthId(

    @NotBlank(message = "Auth ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Auth ID must not exceed {max} characters")
    String authId

  ) {
  }

  record CreateUser(

    @NotBlank(message = "Auth ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Auth ID must not exceed {max} characters")
    String authId,

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = Constants.EMAIL_LENGTH, message = "Email must not exceed {max} characters")
    String email,

    @NotBlank(message = "Display name must not be blank")
    @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Display name must not exceed {max} characters")
    String displayName,

    @NotNull(message = "LDAP role must not be null")
    UserRole ldapRole,

    @NotNull(message = "Application role must not be null")
    UserRole applicationRole,

    boolean active

  ) {
  }

  record UpdateUser(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = Constants.EMAIL_LENGTH, message = "Email must not exceed {max} characters")
    String email,

    @NotBlank(message = "Display name must not be blank")
    @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Display name must not exceed {max} characters")
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

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @Nullable
    UserRole role

  ) {
  }

  record UpdateUserProfileVisibility(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    boolean profilePublic

  ) {
  }


  record DeleteUser(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId

  ) {
  }
}


