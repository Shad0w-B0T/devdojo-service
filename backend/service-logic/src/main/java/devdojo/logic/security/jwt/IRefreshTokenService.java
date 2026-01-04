package devdojo.logic.security.jwt;

import java.util.Optional;

public interface IRefreshTokenService {

  String issue(final String authId);

  Optional<String> rotate(final String refreshToken);

  void revoke(final String refreshToken);

}
