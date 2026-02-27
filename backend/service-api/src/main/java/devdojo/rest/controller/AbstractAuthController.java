package devdojo.rest.controller;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.jwt.IJwtBlacklistService;
import devdojo.logic.security.jwt.IJwtService;
import devdojo.logic.security.jwt.IRefreshTokenService;
import devdojo.logic.security.jwt.RefreshCookie;
import devdojo.logic.security.ldap.service.ILdapService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api")
public abstract class AbstractAuthController implements IAuthController {

  private static final Duration REFRESH_TTL = Duration.ofDays(14);

  private final AuthenticationManager authenticationManager;
  private final ILdapService ldapService;
  private final IJwtService jwtService;
  private final IJwtBlacklistService jwtBlacklistService;

  private final IRefreshTokenService refreshTokenService;
  private final RefreshCookie refreshCookie;

  public AbstractAuthController(final AuthenticationManager authenticationManager,
                                final ILdapService ldapService,
                                final IJwtService jwtService,
                                final IJwtBlacklistService jwtBlacklistService,
                                final IRefreshTokenService refreshTokenService,
                                final RefreshCookie refreshCookie) {
    this.authenticationManager = authenticationManager;
    this.ldapService = ldapService;
    this.jwtService = jwtService;
    this.jwtBlacklistService = jwtBlacklistService;
    this.refreshTokenService = refreshTokenService;
    this.refreshCookie = refreshCookie;
  }

  @PostMapping("/login")
  @Override
  public ResponseEntity<JwtResponse> login(@RequestBody final LoginRequest request,
                                                          final HttpServletResponse response) {
    final Authentication auth = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    final UserEntity userEntity = ldapService.loadOrSyncUser(auth.getName());
    final String accessToken = jwtService.generateToken(userEntity);

    final String refreshToken = refreshTokenService.issue(userEntity.authId());
    this.refreshCookie.set(response, refreshToken, REFRESH_TTL.toSeconds());

    return ResponseEntity.ok(new JwtResponse(accessToken));
  }

  @PostMapping("/refresh")
  @Override
  public ResponseEntity<JwtResponse> refresh(final HttpServletRequest request,
                                             final HttpServletResponse response) {
    final String rt = readCookie(request, this.refreshCookie.name()).orElse(null);

    final Optional<String> authIdOpt = refreshTokenService.rotate(rt);
    if (authIdOpt.isEmpty()) {
      this.refreshCookie.clear(response);
      return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }

    final String authId = authIdOpt.get();

    final String newRefresh = this.refreshTokenService.issue(authId);
    this.refreshCookie.set(response, newRefresh, REFRESH_TTL.toSeconds());

    final UserEntity userEntity = this.ldapService.loadOrSyncUser(authId);

    final String newAccess = this.jwtService.generateToken(userEntity);
    return ResponseEntity.ok(new JwtResponse(newAccess));
  }


  @PostMapping("/logout")
  @Override
  public ResponseEntity<Void> logout(final HttpServletRequest request,
                                     final HttpServletResponse response,
                                     @RequestHeader(value = "Authorization", required = false)
                                       final String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      final String accessToken = authHeader.substring(7);
      this.jwtBlacklistService.blacklist(accessToken);
    }

    readCookie(request, this.refreshCookie.name()).ifPresent(this.refreshTokenService::revoke);
    this.refreshCookie.clear(response);

    return ResponseEntity.ok().build();
  }

  private Optional<String> readCookie(final HttpServletRequest request,
                                      final String name) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }
    return Arrays.stream(request.getCookies())
      .filter(c -> name.equals(c.getName()))
      .map(Cookie::getValue)
      .findFirst();
  }
}
