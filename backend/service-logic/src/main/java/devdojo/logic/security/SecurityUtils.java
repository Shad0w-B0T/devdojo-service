package devdojo.logic.security;

import devdojo.logic.security.client.IClient;
import devdojo.schema.exception.permissions.UnauthorizedException;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.function.Supplier;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

  private SecurityUtils() {
  }

  public static @NotNull IClient localThreadClient() {
    final SecurityContext context = SecurityContextHolder.getContext();

    if (context == null) {
      throw new UnauthorizedException("You are not logged in.");
    }

    final Authentication authentication = context.getAuthentication();

    if (authentication == null) {
      throw new UnauthorizedException("" +
        "You are not logged in, no authentication found.");
    }

    if (authentication instanceof IClient client) {
      return client;
    }

    throw new UnauthorizedException("Invalid authenticated type.");
  }

  /**
   * Sanitizes the provided string to remove any potentially harmful HTML or JavaScript content.
   *
   * @param value    The string to sanitize. Cannot be null.
   * @param supplier A supplier for the {@link PolicyFactory} to use. If null, a default policy will be applied.
   * @return A sanitized version of the string, safe for HTML display.
   * @throws NullPointerException if the provided string is null.
   */
  public static @NotNull String owaspHtmlSanitize(final String value,
                                                  final Supplier<PolicyFactory> supplier) {
    Objects.requireNonNull(value);
    final PolicyFactory policyFactory;

    if (supplier != null) {
      policyFactory = supplier.get();
    } else {
      policyFactory = new HtmlPolicyBuilder()
        .toFactory();
    }

    return policyFactory.sanitize(value);
  }
}
