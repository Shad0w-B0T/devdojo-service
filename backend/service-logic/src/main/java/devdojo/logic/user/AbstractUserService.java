package devdojo.logic.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.database.user.UserRepository;
import devdojo.logic.security.SecurityUtils;
import devdojo.schema.annotation.SelfRequired;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.DeletionException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthenticatedException;
import devdojo.schema.user.UserRole;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

  /**
   * Retrieves an existing user from the system.
   * <p>
   * This method attempts to find a user based on the provided uuid.
   * If the user is not found, an exception is thrown.
   *
   * @param userGet the uuid required to find a user
   * @return the found {@link UserEntity}
   * @throws NotFoundException if no user exists with the provided ID
   */

  @Override
  @Transactional
  public UserEntity getUser(final UserGet userGet) {
    if (userGet.authId() != null) {
      return this.userRepository.findByAuthId(userGet.authId())
        .orElseThrow(() -> new NotFoundException("No user found with authId: " + userGet.authId()));
    }

    return this.userElseThrow(userGet.userId());
  }

  /**
   * Creates a new user in the system.
   *
   * <p>This method ensures that a user with the given display name does not already exist
   * before proceeding with user creation. If a user with the same display name exists,
   * a {@link AlreadyExistsException} is thrown.
   *
   * @param userCreate the data required to create a new user; must not be null
   * @return the created {@link UserEntity} instance
   * @throws NullPointerException   if {@code userData} is null
   * @throws AlreadyExistsException if a user with the given display name already exists
   */
  @Override
  @Transactional
  public UserEntity createUser(final UserCreate userCreate) {
    if (this.userRepository.existsByDisplayName(userCreate.displayName())) {
      throw new AlreadyExistsException(userCreate.displayName());
    }

    log.info("Created new user with name: '{}'.", userCreate.displayName());

    final UserEntity userEntity;
    try {
      userEntity = this.userRepository.save(new UserEntity(
        userCreate.authId(),
        userCreate.email(),
        userCreate.displayName(),
        userCreate.role(),
        userCreate.active(),
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
   * @param userUpdate the updated user data; must not be {@code null}
   * @return the updated {@link UserEntity} instance
   * @throws NullPointerException if {@code userData} is {@code null}
   * @throws NotFoundException    if no user exists with the provided ID
   */
  //Todo
  //@SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Transactional
  @Override
  public UserEntity updateUser(@P("user") final UserUpdate userUpdate) {
    final UserEntity userEntity = this.userElseThrow(userUpdate.userId());

    if (!userEntity.displayName().equals(userUpdate.displayName()) &&
      this.userRepository.existsByDisplayName(userUpdate.displayName())) {
      throw new AlreadyExistsException(userUpdate.displayName());
    }

    if (userEntity.role() != userUpdate.role()) {
      if (SecurityUtils.currentTokenDetails().userRole().id() < userUpdate.role().id()) {
        throw new UnauthenticatedException("You do not have enough permissions to set the role.");
      }
    }

    userEntity.email(userUpdate.email());
    userEntity.displayName(userUpdate.displayName());
    userEntity.role(userUpdate.role());
    userEntity.active(userUpdate.active());
    userEntity.lastLogin(userUpdate.lastLogin());

    final UserEntity updatedUserEntity;
    try {
      updatedUserEntity = this.userRepository.save(userEntity);
      log.info("User {} successfully updated.", updatedUserEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while updating user.", exception);
    }
    return updatedUserEntity;
  }

  //Todo
  //@SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Transactional
  @Override
  public UserEntity updateUser(@P("user") final UserDisplayNameUpdate userDisplayNameUpdate) {
    final UserEntity userEntity = this.userElseThrow(userDisplayNameUpdate.userId());

    return this.updateUser(new UserUpdate() {
      @Override
      public UUID userId() {
        return userEntity.id();
      }

      @Override
      public String email() {
        return userEntity.email();
      }

      @Override
      public String displayName() {
        return userDisplayNameUpdate.displayName();
      }

      @Override
      public UserRole role() {
        return userEntity.role();
      }

      @Override
      public boolean active() {
        return userEntity.active();
      }

      @Override
      public Date lastLogin() {
        return userEntity.lastLogin();
      }
    });
  }

  @Override
  public UserEntity updateUser(@P("user") final UserRoleUpdate userRoleUpdate) {
    final UserEntity userEntity = this.userElseThrow(userRoleUpdate.userId());

    return this.updateUser(new UserUpdate() {
      @Override
      public UUID userId() {
        return userEntity.id();
      }

      @Override
      public String email() {
        return userEntity.email();
      }

      @Override
      public String displayName() {
        return userEntity.displayName();
      }

      @Override
      public UserRole role() {
        return userRoleUpdate.role();
      }

      @Override
      public boolean active() {
        return userEntity.active();
      }

      @Override
      public Date lastLogin() {
        return userEntity.lastLogin();
      }
    });
  }

  /**
   * Deletes an existing user from the system.
   *
   * <p>This method retrieves an ID and attempts to delete them from the database.
   * If the deletion fails (i.e., the user still exists in the database after deletion),
   * a {@link DeletionException} is thrown.
   *
   * @param userDelete the ID of the user to delete
   * @return {@code true} if the user was successfully deleted
   * @throws NotFoundException if no user exists with the provided ID
   * @throws DeletionException if the user was not successfully deleted
   */
  //Todo
  //@SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Transactional
  @Override
  public UserEntity deleteUser(@P("user") final UserDelete userDelete) {
    final UUID id = userDelete.userId();
    final UserEntity userEntity = this.userElseThrow(id);

    try {
      this.userRepository.delete(userEntity);
      log.info("User {} successfully deleted.", userEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while deleting user.", exception);
    }

    // check if user was successfully deleted from db
    if (this.userRepository.existsById(id)) {
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
  private @NotNull UserEntity userElseThrow(final UUID id) {
    log.info("Trying to get user with uuid: '{}'.", id);
    return this.userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
  }
}
