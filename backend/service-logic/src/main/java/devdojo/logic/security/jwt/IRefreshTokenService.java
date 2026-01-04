package devdojo.logic.security.jwt;

import java.util.Optional;

public interface IRefreshTokenService {

  /**
   * Creates a refresh token for the given authId and stores it server-side.
   * Returns the opaque refresh token string.
   */
  String issue(String authId);

  /**
   * Consumes/rotates a refresh token:
   * - validates token exists and not revoked
   * - revokes old token
   * - issues a new token (rotation)
   * Returns authId if valid.
   */
  Optional<String> rotate(String refreshToken);

  /**
   * Revokes a refresh token (logout).
   */
  void revoke(String refreshToken);

}
