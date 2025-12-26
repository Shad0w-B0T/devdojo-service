package devdojo.logic.security.web;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.user.IUserService;
import devdojo.schema.user.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public abstract class AbstractWebFilter extends OncePerRequestFilter implements IWebFilter {

  private final @NotNull IUserService userService;

  protected AbstractWebFilter(final IUserService userService) {
    Objects.requireNonNull(userService, "For the filter, the userService must be provided.");

    this.userService = userService;
    log.info("Web filter for supabase evaluator.");
  }

  @Override
  protected void doFilterInternal(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response,
                                  @NotNull final FilterChain filterChain) throws ServletException, IOException {
    final String userAgent = SecurityUtils.owaspHtmlSanitize(request.getHeader(HttpHeaders.USER_AGENT), null);

    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println(authentication.getPrincipal());
    System.out.println(authentication.isAuthenticated());

    //Set fingerprint for logs.
    if (authentication.getPrincipal() != null) {
      MDC.put("user", authentication.getPrincipal().toString());
    }

    filterChain.doFilter(request, response);
  }

/*  private UserEntity userEntityFromRequest(final ISupabaseUser supabaseUser) {
    try {
      final UserEntity userEntity = this.userService.getUser(new GetUserBySupabase(supabaseUser.id()));

      return this.userService.updateUser(
        new UpdateUser(userEntity, supabaseUser.email(), supabaseUser.aal(), new Date()));
    } catch (final NotFoundException exception) {
      final boolean admin = this.userService.isAdminNeeded();

      return this.userService.createUser(new CreateUser(supabaseUser.id(), supabaseUser.email(),
        supabaseUser.username(), admin ? UserRole.ADMIN : UserRole.USER, true, supabaseUser.aal()));
    }
  }

  private record GetUserByAuthId(String authId) implements IUserService.UserGet {

    @Override
    public UUID userId() {
      return null;
    }

  }*/

  private record CreateUser(String authId,
                            String email,
                            String displayName,
                            UserRole role,
                            boolean active) implements IUserService.UserCreate {

  }

  private record UpdateUser(UserEntity userEntity,
                            String email,
                            Date lastLogin) implements IUserService.UserUpdate {
    @Override
    public UUID userId() {
      return this.userEntity.id();
    }

    @Override
    public String email() {
      return this.email;
    }

    @Override
    public String displayName() {
      return this.userEntity.displayName();
    }

    @Override
    public UserRole role() {
      return this.userEntity.role();
    }

    @Override
    public boolean active() {
      return this.userEntity.active();
    }

    @Override
    public Date lastLogin() {
      return this.lastLogin;
    }
  }
}
