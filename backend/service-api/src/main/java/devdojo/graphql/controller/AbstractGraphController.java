package devdojo.graphql.controller;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.schema.exception.permissions.UnauthorizedException;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public abstract class AbstractGraphController {

  private static UserEntity ANON_DUMMY;

  static {
    ANON_DUMMY = new UserEntity("anon",
      "anon@hdm-stuttgart.de",
      "anonymous user",
      UserRole.GUEST,
      UserRole.GUEST, true,
      new Date(), true);
  }

  protected @NotNull UserEntity anonUser() {
    return ANON_DUMMY;
  }

  protected boolean isSelf(final String userId) {
    if (userId == null) {
      return false;
    }

    try {
      return SecurityUtils.currentTokenDetails().userId().equals(userId);
    } catch (final UnauthorizedException exception) {
      return false;
    }
  }

  protected UserEntity anonymousUser(final UserEntity input) {
    final UserEntity userEntity = input;

    if (!this.isSelf(userEntity.externalId()) && !userEntity.profilePublic()) {
      return this.anonUser();
    }

    return userEntity;
  }
}
