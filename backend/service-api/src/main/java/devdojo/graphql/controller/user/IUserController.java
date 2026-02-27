package devdojo.graphql.controller.user;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.user.IUserService;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface IUserController {

  // ────── Queries ──────

  @NotNull
  List<UserEntity> users();

  @NotNull
  long usersActiveCount();

  @NotNull
  UserEntity userSelf();

  @NotNull
  UserEntity user(@NotNull final IUserService.GetUser input);

  // ────── Mutations ──────

  @NotNull
  UserEntity updateUserRole(@NotNull final IUserService.UpdateUserRole input);

  @NotNull
  UserEntity updateUserVisibility(@NotNull final IUserService.UpdateUserProfileVisibility input);
  // ────── Schema ──────

  //User
  @NotNull(message = "ID cannot be null.")
  String id(@NotNull final UserEntity userEntity);

  @NotNull(message = "Email cannot be null.")
  String email(@NotNull final UserEntity userEntity);

  @NotNull(message = "Name cannot be null.")
  String name(@NotNull final UserEntity userEntity);

  @NotNull(message = "Role cannot be null.")
  UserRole role(@NotNull final UserEntity userEntity);

  @NotNull
  boolean active(@NotNull final UserEntity userEntity);

  @NotNull
  long lastLogin(@NotNull final UserEntity userEntity);

  @NotNull
  boolean profilePublic(@NotNull final UserEntity userEntity);

  @NotNull
  long createdAt(@NotNull final UserEntity userEntity);

  @NotNull
  long updatedAt(@NotNull final UserEntity userEntity);
}
