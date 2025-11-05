package devdojo.logic.security.web;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.security.client.Client;
import devdojo.logic.security.client.IClient;
import devdojo.logic.security.supabase.ISupabaseEvaluator;
import devdojo.logic.security.supabase.ISupabaseUser;
import devdojo.logic.user.IUserService;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthenticatedException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public abstract class AbstractWebFilter extends OncePerRequestFilter implements IWebFilter {

  private final @NotNull ISupabaseEvaluator supabaseEvaluator;
  private final @NotNull IUserService userService;
  private final @NotNull IClient internal;

  protected AbstractWebFilter(final ISupabaseEvaluator supabaseEvaluator,
                              final IUserService userService) {
    Objects.requireNonNull(supabaseEvaluator, "For the filter, the supabase evaluator must be provided.");
    Objects.requireNonNull(userService, "For the filter, the userService must be provided.");

    this.supabaseEvaluator = supabaseEvaluator;
    this.userService = userService;

    final UserEntity internalUser = new UserEntity(UUID.randomUUID(), "", "",
      UserRole.SYSTEM, true, false, new Date());
    internalUser.id(UUID.randomUUID());
    this.internal = new Client("", internalUser);
    log.info("Web filter for supabase evaluator.");
  }

  @Override
  protected void doFilterInternal(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response,
                                  @NotNull final FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    if (path.startsWith("/ws/")) {
      filterChain.doFilter(request, response);
      return;
    }
    
    final String userAgent = SecurityUtils.owaspHtmlSanitize(request.getHeader(HttpHeaders.USER_AGENT), null);

    SecurityContextHolder.getContext().setAuthentication(internal);
    try {
      final ISupabaseUser user = this.supabaseEvaluator.authenticate(request);
      final UserEntity userEntity = this.userEntityFromRequest(user);
      final IClient client = new Client(userAgent, userEntity);

      //Set fingerprint for logs.
      MDC.put("user", userEntity.id().toString());

      SecurityContextHolder.getContext().setAuthentication(client);
    } catch (final UnauthenticatedException exception) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private UserEntity userEntityFromRequest(final ISupabaseUser supabaseUser) {
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

  private record GetUserBySupabase(UUID supabaseId) implements IUserService.UserGet {

    @Override
    public UUID userId() {
      return null;
    }

  }

  private record CreateUser(UUID supabaseId,
                            String supabaseEmail,
                            String displayName,
                            UserRole role,
                            boolean active,
                            boolean emailVerified) implements IUserService.UserCreate {

  }

  private record UpdateUser(UserEntity userEntity,
                            String supabaseEmail,
                            boolean emailVerified,
                            Date lastLogin) implements IUserService.UserUpdate {

    @Override
    public UUID userId() {
      return this.userEntity.id();
    }

    @Override
    public String supabaseEmail() {
      return this.supabaseEmail;
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
    public boolean emailVerified() {
      return this.emailVerified;
    }

    @Override
    public Date lastLogin() {
      return this.lastLogin;
    }
  }
}
