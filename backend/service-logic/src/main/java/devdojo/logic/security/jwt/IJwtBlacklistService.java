package devdojo.logic.security.jwt;

public interface IJwtBlacklistService {

  void blacklist(final String token);

  boolean isBlacklisted(final String jti);

}
