package devdojo.logic.security.supabase;

import devdojo.schema.exception.permissions.UnauthenticatedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

/**
 * An abstract base class for security modules that implements the {@link ISupabaseEvaluator} interface.
 * <p>
 * This class provides common functionality for initializing security modules, such as logging
 * the initialization of the security module.
 * </p>
 */
@Slf4j
public abstract class AbstractSupabaseEvaluator implements ISupabaseEvaluator {

  private final static int PREFIX_LENGTH = 7;

  private final @NotNull JwtParser parser;

  public AbstractSupabaseEvaluator(final String algorithm,
                                   final String secret) {
    Objects.requireNonNull(algorithm);
    Objects.requireNonNull(secret);

    final SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), algorithm);
    this.parser = Jwts.parser()
      .verifyWith(secretKeySpec)
      .build();

    log.info("Successfully initialized superbase security module.");
  }


  @Override
  public @NotNull ISupabaseUser authenticate(@NotNull final HttpServletRequest request)
    throws UnauthenticatedException {
    final String value = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (value == null || !value.startsWith("Bearer ")) {
      throw new UnauthenticatedException("No auth header present or wrong type.");
    }

    final String accessToken = value.substring(PREFIX_LENGTH);
    try {
      final Jws<Claims> jwsClaims = this.parser.parseSignedClaims(accessToken);
      final Claims claims = jwsClaims.getPayload();

      final String id = (String) claims.get("sub");
      final HashMap<Object, Object> meta = claims.get("user_metadata", HashMap.class);
      final String email = (String) meta.get("email");
      final String username = (String) meta.get("username");
      final boolean active = claims.get("aal").equals("aal2");

      return new SupabaseUser(UUID.fromString(id), email, username, active);
    } catch (final ExpiredJwtException | MalformedJwtException | SecurityException |
                   IllegalArgumentException | UnsupportedJwtException exception) {
      throw new UnauthenticatedException("Request is unauthorized.", exception);
    }
  }

  private record SupabaseUser(@NotNull UUID id,
                                     @NotNull String email,
                                     @NotNull String username,
                                     boolean aal) implements ISupabaseUser {

  }
}
