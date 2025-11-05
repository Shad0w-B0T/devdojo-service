package devdojo.logic.security.client;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IClientEvaluator {

  boolean isValid(@NotNull final IClient client) throws UsernameNotFoundException;

  boolean hasRole(@NotNull final IClient client,
                  @NotNull final UserRole role) throws UsernameNotFoundException;

  boolean isSelf(@NotNull final IClient client,
                 @NotNull final UUID userId,
                 @NotNull final UserRole bypassRole);
}
