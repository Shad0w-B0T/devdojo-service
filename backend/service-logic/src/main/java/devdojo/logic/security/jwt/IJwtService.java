package devdojo.logic.security.jwt;

import devdojo.logic.database.user.UserEntity;
import devdojo.schema.user.UserRole;
import java.time.Duration;

public interface IJwtService {

  String generateToken(final UserEntity userEntity);

  String extractUsername(final String token);

  String extractJti(final String token);

  Duration getRemainingValidityDuration(final String token);

  UserRole extractRole(final String token);
}
