package devdojo.graphql.input.user;

import devdojo.logic.user.IUserService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateUserName(@NotNull UUID userId,
                             @NotNull String name) implements IUserService.UserDisplayNameUpdate {
  @Override
  public String displayName() {
    return this.name;
  }
}
