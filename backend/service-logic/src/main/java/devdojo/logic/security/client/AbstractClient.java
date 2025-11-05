package devdojo.logic.security.client;

import devdojo.schema.user.IUser;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Slf4j
@Getter
@Accessors(fluent = true)
public abstract class AbstractClient extends AbstractAuthenticationToken implements IClient {

  private final @NotNull String userAgent;
  private final @NotNull IUser user;

  protected AbstractClient(final String userAgent,
                           final IUser user) {
    super(Collections.emptyList());
    Objects.requireNonNull(userAgent, "userAgent cannot be null");
    Objects.requireNonNull(user, "user cannot be null");

    this.userAgent = userAgent;
    this.user = user;
    this.setAuthenticated(true);
  }

  @Override
  public @NotNull IUser getPrincipal() {
    return user;
  }

  public List<UserRole> getCredentials() {
    return List.of(UserRole.values());
  }

  @Override
  public @NotNull UUID userId() {
    return this.user.id();
  }

  @Override
  public @NotNull UserRole userRole() {
    return this.user.role();
  }
}
