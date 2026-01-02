package devdojo.logic.security.web;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.security.details.ITokenDetails;
import devdojo.logic.security.details.TokenDetails;
import devdojo.logic.security.ldap.ILdapService;
import devdojo.logic.user.IUserService;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.user.UserRole;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public abstract class AbstractWebFilter extends OncePerRequestFilter implements IWebFilter {

  private final @NotNull IUserService userService;
  private final @Nullable ILdapService ldapService;

  protected AbstractWebFilter(final IUserService userService,
                              @Nullable final ILdapService ldapService) {
    Objects.requireNonNull(userService, "For the filter, the userService must be provided.");

    this.userService = userService;
    this.ldapService = ldapService;
    log.info("Web filter for supabase evaluator.");
  }

  @Override
  protected void doFilterInternal(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response,
                                  @NotNull final FilterChain filterChain) throws ServletException, IOException {
    final String userAgent = SecurityUtils.owaspHtmlSanitize(request.getHeader(HttpHeaders.USER_AGENT), null);

    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof AbstractAuthenticationToken token &&
      token.getPrincipal() instanceof LdapUserDetailsImpl ldapUserDetails &&
      this.ldapService != null) {

      final String authId = ldapUserDetails.getUsername();
      final boolean active = ldapUserDetails.isEnabled() && ldapUserDetails.isAccountNonExpired() &&
        ldapUserDetails.isCredentialsNonExpired() && ldapUserDetails.isAccountNonLocked();

      final Map<String, Object> ldapAttributes = this.ldapService.loadUserAttributes(authId);
      final String displayName = ldapAttributes.getOrDefault("displayName", "").toString();
      final String email = ldapAttributes.getOrDefault("mail", "").toString();
      final UserRole role = this.mapLdapUserRole(ldapAttributes.get("eduPersonAffiliation"));

      final UserEntity userEntity = this.updateUserEntity(authId, email, displayName, active, role);
      final ITokenDetails tokenDetails = new TokenDetails(userEntity.id(), authId, email, displayName, role);
      token.setDetails(tokenDetails);

      //Set fingerprint for logs.
      MDC.put("user", authId);
    }

    filterChain.doFilter(request, response);
  }

  private UserEntity updateUserEntity(final String authId,
                                      final String email,
                                      final String displayName,
                                      final boolean active,
                                      final UserRole userRole) {
    try {
      final UserEntity userEntity = this.userService.getUser(new IUserService.UserGet() {
        @Override
        public UUID userId() {
          return null;
        }

        @Override
        public String authId() {
          return authId;
        }
      });

      return this.userService.updateUser(new IUserService.UserUpdate() {
        @Override
        public UUID userId() {
          return userEntity.id();
        }

        @Override
        public String email() {
          return email;
        }

        @Override
        public String displayName() {
          return displayName;
        }

        @Override
        public UserRole role() {
          return userRole;
        }

        @Override
        public boolean active() {
          return active;
        }

        @Override
        public Date lastLogin() {
          return new Date();
        }
      });
    } catch (final NotFoundException exception) {
      final boolean admin = this.userService.isAdminNeeded();

      return this.userService.createUser(new IUserService.UserCreate() {
        @Override
        public String authId() {
          return authId;
        }

        @Override
        public String email() {
          return email;
        }

        @Override
        public String displayName() {
          return displayName;
        }

        @Override
        public UserRole role() {
          return userRole;
        }

        @Override
        public boolean active() {
          return active;
        }
      });
    }
  }

  private UserRole mapLdapUserRole(final Object attributeObject) {

    if (!(attributeObject instanceof List<?> list)) {
      return UserRole.GUEST;
    }

    if (list.contains("student")) {
      return UserRole.STUDENT;
    }

    if (list.contains("employee") || list.contains("staff")) {
      return UserRole.ADMIN;
    }

    return UserRole.GUEST;
  }

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
