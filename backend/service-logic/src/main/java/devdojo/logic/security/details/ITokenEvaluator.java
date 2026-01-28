package devdojo.logic.security.details;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ITokenEvaluator {

  boolean isValid(@NotNull final AbstractAuthenticationToken token) throws UsernameNotFoundException;

  boolean hasRole(@NotNull final AbstractAuthenticationToken token,
                  @NotNull final UserRole role) throws UsernameNotFoundException;
  
  boolean isSelf(@NotNull final AbstractAuthenticationToken token,
                 @NotNull final String userId,
                 @NotNull final UserRole bypassRole);
}
