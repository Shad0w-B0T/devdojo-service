package devdojo.logic.security.web;


import devdojo.logic.security.jwt.IJwtBlacklistService;
import devdojo.logic.security.jwt.IJwtService;
import devdojo.schema.user.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class AbstractJwtAuthenticationFilter extends OncePerRequestFilter implements IWebFilter {

  private final IJwtService jwtService;
  private final IJwtBlacklistService blacklistService;

  protected AbstractJwtAuthenticationFilter(final IJwtService jwtService,
                                            final IJwtBlacklistService blacklistService) {
    this.jwtService = jwtService;
    this.blacklistService = blacklistService;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain) throws ServletException, IOException {
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith("Bearer ")) {
      final String token = header.substring(7);

      if (blacklistService.isBlacklisted(jwtService.extractJti(token))) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      final String authId = jwtService.extractUsername(token);
      final UserRole role = jwtService.extractRole(token);

      final UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(authId, null,
          List.of(new SimpleGrantedAuthority("ROLE_" + role)));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}
