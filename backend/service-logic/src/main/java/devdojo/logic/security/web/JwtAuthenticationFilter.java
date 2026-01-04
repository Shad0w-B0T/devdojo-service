package devdojo.logic.security.web;

import devdojo.logic.security.jwt.JwtBlacklistService;
import devdojo.logic.security.jwt.JwtService;
import org.springframework.stereotype.Service;

@Service("jwt")
public class JwtAuthenticationFilter extends AbstractJwtAuthenticationFilter {
  protected JwtAuthenticationFilter(final JwtService jwtService,
                                    final JwtBlacklistService blacklistService) {
    super(jwtService, blacklistService);
  }
}
