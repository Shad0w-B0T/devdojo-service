package devdojo.graphql.input.user;

import devdojo.logic.user.IUserService;
import java.util.UUID;

public record GetUser(UUID userId) implements IUserService.UserGet {
  @Override
  public String authId() {
    return null;
  }
}
