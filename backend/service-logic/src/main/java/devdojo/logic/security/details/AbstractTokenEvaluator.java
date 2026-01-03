package devdojo.logic.security.details;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractTokenEvaluator implements ITokenEvaluator {

  @Override
  public boolean isValid(@NotNull final AbstractAuthenticationToken token) throws UsernameNotFoundException {
    if (!token.isAuthenticated()) {
      throw new UsernameNotFoundException("Authentication is not valid.");
    }
    return true;
  }

  @Override
  public boolean hasRole(@NotNull final AbstractAuthenticationToken token,
                         @NotNull final UserRole role) throws UsernameNotFoundException {
    if (!(token.getDetails() instanceof ITokenDetails tokenDetails)) {
      return false;
    }
    return tokenDetails.userRole().id() >= role.id();
  }

  @Override
  public boolean isSelf(@NotNull final AbstractAuthenticationToken token,
                        @NotNull final String userId,
                        @NotNull final UserRole bypassRole) {
    if (!(token.getDetails() instanceof ITokenDetails tokenDetails)) {
      return false;
    }

    if (tokenDetails.userId().equals(UUID.fromString(userId))) {
      return true;
    }

    return this.hasRole(token, bypassRole);
  }
}
