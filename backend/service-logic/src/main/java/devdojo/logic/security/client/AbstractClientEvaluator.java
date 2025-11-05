package devdojo.logic.security.client;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractClientEvaluator implements IClientEvaluator {

  @Override
  public boolean isValid(@NotNull final IClient client) throws UsernameNotFoundException {
    if (!client.isAuthenticated()) {
      throw new UsernameNotFoundException("Authentication is not valid.");
    }
    return true;
  }

  @Override
  public boolean hasRole(@NotNull final IClient client,
                         @NotNull final UserRole role) throws UsernameNotFoundException {
    return client.userRole().id() >= role.id();
  }

  @Override
  public boolean isSelf(@NotNull final IClient client,
                        @NotNull final UUID userId,
                        @NotNull final UserRole bypassRole) {
    if (client.userId().equals(userId)) {
      return true;
    }

    return client.userRole().id() >= bypassRole.id();
  }
}
