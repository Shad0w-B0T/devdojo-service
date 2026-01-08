package devdojo.logic.security.web;

import devdojo.logic.security.jwt.IJwtBlacklistService;
import devdojo.logic.security.jwt.IJwtService;
import org.springframework.stereotype.Service;

@Service("jwt")
public class JwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {
  protected JwtAuthenticationFilter(final IJwtService jwtService,
                                    final IJwtBlacklistService blacklistService) {
    super(jwtService, blacklistService);
  }
}
