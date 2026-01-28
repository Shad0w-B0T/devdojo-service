package devdojo.logic.security.web;


import devdojo.logic.security.jwt.IJwtBlacklistService;
import devdojo.logic.security.jwt.IJwtService;
import devdojo.schema.user.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class AbstractJwtAuthenticationFilter extends OncePerRequestFilter implements IWebFilter,
  ChannelInterceptor {

  private final IJwtService jwtService;
  private final IJwtBlacklistService blacklistService;

  protected AbstractJwtAuthenticationFilter(final IJwtService jwtService,
                                            final IJwtBlacklistService blacklistService) {
    this.jwtService = jwtService;
    this.blacklistService = blacklistService;
  }

  protected UsernamePasswordAuthenticationToken authenticateToken(final String token) {
    try {
      if (this.blacklistService.isBlacklisted(this.jwtService.extractJti(token))) {
        throw new AccessDeniedException("JWT is blacklisted");
      }

      final String authId = this.jwtService.extractUsername(token);
      final UserRole role = this.jwtService.extractRole(token);

      return new UsernamePasswordAuthenticationToken(
        authId,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_" + role))
      );
    } catch (final ExpiredJwtException exception) {
      throw new AccessDeniedException("JWT expired", exception);
    }
  }

  @Override
  protected void doFilterInternal(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final FilterChain filterChain
  ) throws ServletException, IOException {

    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith("Bearer ")) {
      final String token = header.substring(7);

      try {
        SecurityContextHolder.getContext().setAuthentication(authenticateToken(token));
      } catch (AccessDeniedException ex) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }


  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final StompHeaderAccessor accessor =
      MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      final String authHeader = accessor.getFirstNativeHeader("Authorization");

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        final String token = authHeader.substring(7);
        try {
          accessor.setUser(authenticateToken(token));
        } catch (final AccessDeniedException ex) {
          /*ignoring error -> no user wil be set, so no user context is given to websocket.*/
        }
      }
    }

    return message;
  }
}
