package devdojo.logic.security.jwt;

import devdojo.logic.database.user.UserEntity;
import devdojo.schema.user.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

public abstract class AbstractJwtService implements IJwtService {

  private final static int EXPIRE_MINUTES = 12;

  private final SecretKey key;

  protected AbstractJwtService(final String secretHash) {
    this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretHash));
  }

  public String generateToken(final UserEntity userEntity) {
    return Jwts.builder()
      .id(UUID.randomUUID().toString())
      .subject(userEntity.authId())
      .claim("role", userEntity.ldapRole().name())
      .issuedAt(new Date())
      .expiration(Date.from(Instant.now().plus(EXPIRE_MINUTES, ChronoUnit.HOURS)))
      .signWith(key)
      .compact();
  }

  public String extractUsername(final String token) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload()
      .getSubject();
  }

  public String extractJti(final String token) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload()
      .getId();
  }

  public Duration getRemainingValidityDuration(final String token) {
    final Instant exp = Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload()
      .getExpiration()
      .toInstant();

    return Duration.between(Instant.now(), exp).isNegative()
      ? Duration.ZERO
      : Duration.between(Instant.now(), exp);
  }

  @Override
  public UserRole extractRole(final String token) {
    final String role = Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload()
      .get("role", String.class);

    return UserRole.valueOf(role);
  }

}
