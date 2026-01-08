package devdojo.rest.controller;

import devdojo.logic.security.jwt.IJwtBlacklistService;
import devdojo.logic.security.jwt.IJwtService;
import devdojo.logic.security.jwt.IRefreshTokenService;
import devdojo.logic.security.jwt.RefreshCookie;
import devdojo.logic.security.ldap.ILdapService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController extends AbstractAuthController {

  public AuthController(final AuthenticationManager authenticationManager,
                        final ILdapService ldapService,
                        final IJwtService jwtService,
                        final IJwtBlacklistService jwtBlacklistService,
                        final IRefreshTokenService refreshTokenService,
                        final RefreshCookie refreshCookie) {
    super(authenticationManager, ldapService, jwtService, jwtBlacklistService, refreshTokenService, refreshCookie);
  }
}
