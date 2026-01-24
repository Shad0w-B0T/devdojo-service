package devdojo.logic.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.database.user.UserRepository;
import devdojo.logic.security.SecurityUtils;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.DeletionException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthenticatedException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;

@Validated
@Transactional
@Slf4j
public abstract class AbstractUserService implements IUserService {

  private final @NotNull UserRepository userRepository;

  protected AbstractUserService(
    @NotNull(message = "UserRepository must not be null") final UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  @Override
  public boolean isAdminNeeded() {
    return this.userRepository.count() == 0;
  }

  @Override
  public List<UserEntity> getUsers() {
    return this.userRepository.findAll();
  }

  @Override
  public UserEntity getUser(final GetUser getUser) {
    return this.userElseThrow(getUser.userId());
  }

  @Override
  public UserEntity getUser(final GetUserByAuthId getUserByAuthId) {
    return this.userRepository.findByAuthId(getUserByAuthId.authId())
      .orElseThrow(() -> new NotFoundException("No user found with authId: " + getUserByAuthId.authId()));
  }

  /**
   * Creates a new user in the system.
   *
   * <p>This method ensures that a user with the given display name does not already exist
   * before proceeding with user creation. If a user with the same display name exists,
   * a {@link AlreadyExistsException} is thrown.
   *
   * @param createUser the data required to create a new user; must not be null
   * @return the created {@link UserEntity} instance
   * @throws NullPointerException   if {@code userData} is null
   * @throws AlreadyExistsException if a user with the given display name already exists
   */
  @Override
  @Transactional
  public UserEntity createUser(final CreateUser createUser) {
    if (this.userRepository.existsByDisplayName(createUser.displayName())) {
      throw new AlreadyExistsException(createUser.displayName());
    }

    log.info("Created new user with name: '{}'.", createUser.displayName());

    final UserEntity userEntity;
    try {
      userEntity = this.userRepository.save(new UserEntity(
        createUser.authId(),
        createUser.email(),
        createUser.displayName(),
        createUser.ldapRole(),
        createUser.ldapRole(), // the initial role is the same as ldap role
        createUser.active(),
        new Date()
      ));
      log.info("User {} successfully created.", userEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while creating user in database.", exception);
    }

    return userEntity;
  }

  /**
   * Updates an existing user in the system.
   *
   * <p>This method retrieves a user by their ID and updates their details, including
   * display name, role, active status, and last login timestamp. The updated entity
   * is then returned but not explicitly saved to the database.
   *
   * @param updateUser the updated user data; must not be {@code null}
   * @return the updated {@link UserEntity} instance
   * @throws NullPointerException if {@code userData} is {@code null}
   * @throws NotFoundException    if no user exists with the provided ID
   */
  //Todo
  //@SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Transactional
  @Override
  public UserEntity updateUser(@P("user") final UpdateUser updateUser) {
    final UserEntity userEntity = this.userElseThrow(updateUser.userId());

    if (!userEntity.displayName().equals(updateUser.displayName()) &&
      this.userRepository.existsByDisplayName(updateUser.displayName())) {
      throw new AlreadyExistsException(updateUser.displayName());
    }

    if (userEntity.applicationRole() != updateUser.applicationRole()) {
      if (SecurityUtils.currentTokenDetails().applicationRole().id() <
        Objects.requireNonNull(updateUser.applicationRole()).id()) {
        throw new UnauthenticatedException("You do not have enough permissions to set the role.");
      }
    }

    userEntity.email(updateUser.email());
    userEntity.displayName(updateUser.displayName());
    userEntity.applicationRole(updateUser.applicationRole());
    userEntity.active(updateUser.active());
    userEntity.lastLogin(updateUser.lastLogin());

    final UserEntity updatedUserEntity;
    try {
      updatedUserEntity = this.userRepository.save(userEntity);
      log.info("User {} successfully updated.", updatedUserEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while updating user.", exception);
    }
    return updatedUserEntity;
  }

  @Override
  public UserEntity updateUser(@P("user") final UpdateUserRole updateUserRole) {
    final UserEntity userEntity = this.userElseThrow(updateUserRole.userId());

    return this.updateUser(new UpdateUser(
      userEntity.externalId(),
      userEntity.email(),
      userEntity.displayName(),
      updateUserRole.role(),
      userEntity.active(),
      userEntity.lastLogin()
    ));
  }

  /**
   * Deletes an existing user from the system.
   *
   * <p>This method retrieves an ID and attempts to delete them from the database.
   * If the deletion fails (i.e., the user still exists in the database after deletion),
   * a {@link DeletionException} is thrown.
   *
   * @param deleteUser the ID of the user to delete
   * @return {@code true} if the user was successfully deleted
   * @throws NotFoundException if no user exists with the provided ID
   * @throws DeletionException if the user was not successfully deleted
   */
  //Todo
  //@SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Transactional
  @Override
  public UserEntity deleteUser(@P("user") final DeleteUser deleteUser) {
    final String id = deleteUser.userId();
    final UserEntity userEntity = this.userElseThrow(id);

    try {
      this.userRepository.delete(userEntity);
      log.info("User {} successfully deleted.", userEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while deleting user.", exception);
    }

    // check if user was successfully deleted from db
    if (this.userRepository.existsByExternalId(id)) {
      throw new DeletionException(id);
    }

    log.info("User {} is now permanently removed.", userEntity.id());

    return userEntity;
  }

  /**
   * Retrieves a {@link UserEntity} by its ID, or throws a {@link NotFoundException} if not found.
   *
   * <p>This method queries the user repository for a {@link UserEntity} by the given UUID.
   * If the user is found, the {@link UserEntity} is returned. If not, a {@link NotFoundException} is thrown.
   *
   * @param id the UUID of the user to be retrieved, must not be {@code null}
   * @return the {@link UserEntity} associated with the specified UUID
   * @throws NullPointerException if the provided {@code id} is {@code null}
   * @throws NotFoundException    if no user is found with the specified ID
   */
  private @NotNull UserEntity userElseThrow(final String id) {
    log.info("Trying to get user with id: '{}'.", id);
    return this.userRepository.findByExternalId(id).orElseThrow(() -> new NotFoundException(id));
  }
}
