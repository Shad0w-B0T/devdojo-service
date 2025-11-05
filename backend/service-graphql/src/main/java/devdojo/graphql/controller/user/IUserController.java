package devdojo.graphql.controller.user;

import devdojo.graphql.input.user.UpdateUserName;
import devdojo.graphql.input.user.UpdateUserRole;
import devdojo.logic.database.user.UserEntity;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface IUserController {

  // ────── Queries ──────

  @NotNull
  List<UserEntity> users();

  @NotNull
  UserEntity userSelf();

  // ────── Mutations ──────

  @NotNull
  UserEntity updateUserName(@NotNull final UpdateUserName input);

  @NotNull
  UserEntity updateUserRole(@NotNull final UpdateUserRole input);

  // ────── Schema ──────

  //User
  @NotNull(message = "ID cannot be null.")
  UUID id(@NotNull final UserEntity userEntity);

  @NotNull(message = "Email cannot be null.")
  String email(@NotNull final UserEntity userEntity);

  @NotNull(message = "Name cannot be null.")
  String name(@NotNull final UserEntity userEntity);

  @NotNull(message = "Role cannot be null.")
  UserRole role(@NotNull final UserEntity userEntity);

  @NotNull
  boolean active(@NotNull final UserEntity userEntity);

  @NotNull
  boolean emailVerified(@NotNull final UserEntity userEntity);

  @NotNull
  long lastLogin(@NotNull final UserEntity userEntity);

  @NotNull
  long createdAt(@NotNull final UserEntity userEntity);

  @NotNull
  long updatedAt(@NotNull final UserEntity userEntity);
}
