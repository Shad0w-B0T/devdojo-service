package devdojo.logic.security.client;

import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;

public interface IClient extends Authentication, CredentialsContainer {

  @NotNull String userAgent();

  @NotNull UUID userId();

  default @NotNull UserRole userRole() {
    return UserRole.GUEST;
  }
}
