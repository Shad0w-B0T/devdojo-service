package devdojo.logic.security.config;

import devdojo.logic.security.web.IWebFilter;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.method.PrePostTemplateDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

public interface ISecurityConfig {
  /**
   * Configures a security filter chain for the specified web filter and HTTP security settings.
   *
   * @param provider
   * @param webFilter the web filter to be applied; must not be null.
   * @param http      the HttpSecurity object used for configuring security features; must not be null.
   * @return the configured security filter chain.
   * @throws Exception if an error occurs during the configuration of the security filter chain.
   */
  @NotNull
  SecurityFilterChain securityFilterChain(@NotNull final AuthenticationProvider provider,
                                          @NotNull final IWebFilter webFilter,
                                          @NotNull final HttpSecurity http) throws Exception;

  /**
   * Provides default template values for pre and post-processing security configurations.
   *
   * @return the default settings for pre- and post-processing templates.
   */
  @NotNull
  PrePostTemplateDefaults prePostTemplateDefaults();
}
