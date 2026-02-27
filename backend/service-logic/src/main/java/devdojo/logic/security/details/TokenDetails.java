package devdojo.logic.security.details;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;

public record TokenDetails(
  @NotNull String userId,
  @NotNull String authId,
  @NotNull String email,
  @NotNull String displayName,
  @NotNull UserRole applicationRole
) implements ITokenDetails {
}
