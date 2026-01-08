package devdojo.graphql.input.user;

import devdojo.logic.user.IUserService;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateUserRole(@NotNull UUID userId,
                             @NotNull UserRole role) implements IUserService.UserRoleUpdate {

}
