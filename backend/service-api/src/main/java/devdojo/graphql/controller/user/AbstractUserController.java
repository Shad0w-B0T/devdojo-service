package devdojo.graphql.controller.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.user.IUserService;
import devdojo.logic.websocket.service.IPresenceService;
import devdojo.schema.annotation.RoleRequired;
import devdojo.schema.annotation.SelfRequired;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.parameters.P;

public abstract class AbstractUserController implements IUserController {

  private final @NotNull(message = "UserService cannot be null.") IUserService userService;
  private final IPresenceService presenceService;


  protected AbstractUserController(@NotNull final IUserService userService,
                                   @NotNull final IPresenceService presenceService) {
    this.userService = userService;
    this.presenceService = presenceService;
  }

  @QueryMapping("users")
  @RoleRequired(role = UserRole.ADMIN)
  @Override
  public @NotNull List<UserEntity> users() {
    return this.userService.getUsers();
  }

  @QueryMapping("usersActiveCount")
  @Override
  public long usersActiveCount() {
    return this.presenceService.getActiveUserCount();
  }

  @QueryMapping("userSelf")
  @Override
  public @NotNull UserEntity userSelf() {
    return this.userService.getUser(SecurityUtils.currentTokenDetails().userId());
  }

  @QueryMapping("user")
  public @NotNull UserEntity user(@Argument("input") final IUserService.GetUserByAuthId input) {
    return this.userService.getUser(input);
  }

  @MutationMapping("updateUserRole")
  @SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Override
  public @NotNull UserEntity updateUserRole(
    @Argument("input") @P("user") @NotNull final IUserService.UpdateUserRole input) {
    return this.userService.updateUser(input);
  }

  @MutationMapping("updateUserVisibility")
  @SelfRequired(field = "#user.userId()", bypassRole = UserRole.ADMIN)
  @Override
  public UserEntity updateUserVisibility(
    @Argument("input") @P("user") @NotNull final IUserService.UpdateUserProfileVisibility input) {
    return this.userService.updateUser(input);
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull String id(@NotNull final UserEntity userEntity) {
    return userEntity.externalId();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull String email(@NotNull final UserEntity userEntity) {
    return userEntity.email();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull String name(@NotNull final UserEntity userEntity) {
    return userEntity.displayName();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull UserRole role(@NotNull final UserEntity userEntity) {
    return userEntity.applicationRole() != null ? userEntity.applicationRole() : userEntity.ldapRole();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull boolean active(@NotNull final UserEntity userEntity) {
    return userEntity.active();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull long lastLogin(@NotNull final UserEntity userEntity) {
    return userEntity.lastLogin().getTime();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public boolean profilePublic(@NotNull final UserEntity userEntity) {
    return userEntity.profilePublic();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull long createdAt(@NotNull final UserEntity userEntity) {
    return userEntity.createdAt().getTime();
  }

  @SchemaMapping(typeName = "User")
  @Override
  public @NotNull long updatedAt(@NotNull final UserEntity userEntity) {
    return userEntity.updatedAt().getTime();
  }
}
