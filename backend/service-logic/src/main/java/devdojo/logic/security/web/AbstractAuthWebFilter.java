package devdojo.logic.security.web;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.details.ITokenDetails;
import devdojo.logic.security.details.TokenDetails;
import devdojo.logic.security.ldap.ILdapService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.*;
import org.slf4j.MDC;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Valid
public abstract class AbstractAuthWebFilter extends OncePerRequestFilter implements IWebFilter {

  private final ILdapService ldapService;

  protected AbstractAuthWebFilter(@Nullable final ILdapService ldapService) {
    this.ldapService = ldapService;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain) throws ServletException, IOException {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof AbstractAuthenticationToken token &&
      token.getPrincipal() instanceof String authId) {

      final UserEntity user = this.ldapService.loadOrSyncUser(authId);
      final ITokenDetails details = new TokenDetails(
        user.externalId(),
        user.authId(),
        user.email(),
        user.displayName(),
        user.applicationRole()
      );

      token.setDetails(details);
      MDC.put("user", authId);
    }

    filterChain.doFilter(request, response);
  }
}
