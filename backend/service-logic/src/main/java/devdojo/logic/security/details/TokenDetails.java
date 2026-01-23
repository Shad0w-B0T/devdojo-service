package devdojo.logic.security.details;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TokenDetails(
  @NotNull UUID userId,
  @NotNull String authId,
  @NotNull String email,
  @NotNull String displayName,
  @NotNull UserRole applicationRole
) implements ITokenDetails {
}
