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
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

  @Cacheable(
    cacheNames = "users",
    key = "#p0.userId()",
    unless = "#result == null"
  )
  @Override
  public UserEntity getUser(final GetUser getUser) {
    return this.userElseThrow(getUser.userId());
  }

  @Cacheable(
    cacheNames = "usersByAuthId",
    key = "#p0.authId()",
    unless = "#result == null"
  )
  @Override
  public UserEntity getUser(final GetUserByAuthId getUserByAuthId) {
    return this.userRepository.findByAuthId(getUserByAuthId.authId())
      .orElseThrow(() -> new NotFoundException("No user found with authId: " + getUserByAuthId.authId()));
  }

  @Cacheable(
    cacheNames = "usersInternal",
    key = "#p0",
    unless = "#result == null"
  )
  @Override
  public UserEntity getInternalUser(final UUID id) {
    return this.userRepository.findById(id).orElseThrow(() -> new NotFoundException("No user found with id: " + id));
  }

  @Caching(evict = {
    @CacheEvict(
      cacheNames = "users",
      key = "#result.externalId()"
    ),
    @CacheEvict(
      cacheNames = "usersByAuthId",
      key = "#result.authId()",
      condition = "#result != null"
    ),
    @CacheEvict(
      cacheNames = "usersInternal",
      key = "#result.id()",
      condition = "#result != null"
    )
  })
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
        new Date(),
        false
      ));
      log.info("User {} successfully created.", userEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while creating user in database.", exception);
    }

    return userEntity;
  }

  @Caching(evict = {
    @CacheEvict(
      cacheNames = "users",
      key = "#result.externalId()"
    ),
    @CacheEvict(
      cacheNames = "usersByAuthId",
      key = "#result.authId()",
      condition = "#result != null"
    ),
    @CacheEvict(
      cacheNames = "usersInternal",
      key = "#result.id()",
      condition = "#result != null"
    )
  })
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
    userEntity.profilePublic(updateUser.profilePublic());

    final UserEntity updatedUserEntity;
    try {
      updatedUserEntity = this.userRepository.save(userEntity);
      log.info("User {} successfully updated.", updatedUserEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while updating user.", exception);
    }
    return updatedUserEntity;
  }

  @Caching(evict = {
    @CacheEvict(
      cacheNames = "users",
      key = "#result.externalId()"
    ),
    @CacheEvict(
      cacheNames = "usersByAuthId",
      key = "#result.authId()",
      condition = "#result != null"
    ),
    @CacheEvict(
      cacheNames = "usersInternal",
      key = "#result.id()",
      condition = "#result != null"
    )
  })
  @Override
  public UserEntity updateUser(@P("user") final UpdateUserRole updateUserRole) {
    final UserEntity userEntity = this.userElseThrow(updateUserRole.userId());

    return this.updateUser(new UpdateUser(
      userEntity.externalId(),
      userEntity.email(),
      userEntity.displayName(),
      updateUserRole.role(),
      userEntity.active(),
      userEntity.lastLogin(),
      userEntity.profilePublic()
    ));
  }

  @Caching(evict = {
    @CacheEvict(
      cacheNames = "users",
      key = "#result.externalId()"
    ),
    @CacheEvict(
      cacheNames = "usersByAuthId",
      key = "#result.authId()",
      condition = "#result != null"
    ),
    @CacheEvict(
      cacheNames = "usersInternal",
      key = "#result.id()",
      condition = "#result != null"
    )
  })
  @Override
  public UserEntity updateUser(final UpdateUserProfileVisibility updateUserRole) {
    final UserEntity userEntity = this.userElseThrow(updateUserRole.userId());

    return this.updateUser(new UpdateUser(
      userEntity.externalId(),
      userEntity.email(),
      userEntity.displayName(),
      userEntity.ldapRole(),
      userEntity.active(),
      userEntity.lastLogin(),
      updateUserRole.profilePublic()
    ));
  }

  @Caching(evict = {
    @CacheEvict(
      cacheNames = "users",
      key = "#result.externalId()"
    ),
    @CacheEvict(
      cacheNames = "usersByAuthId",
      key = "#result.authId()",
      condition = "#result != null"
    ),
    @CacheEvict(
      cacheNames = "usersInternal",
      key = "#result.id()",
      condition = "#result != null"
    )
  })
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

  private @NotNull UserEntity userElseThrow(final String id) {
    return this.userRepository.findByExternalId(id).orElseThrow(() -> new NotFoundException(id));
  }
}
