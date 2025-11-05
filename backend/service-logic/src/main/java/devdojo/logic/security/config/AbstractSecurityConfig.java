package devdojo.logic.security.config;

import devdojo.logic.security.web.IWebFilter;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authorization.method.PrePostTemplateDefaults;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public abstract class AbstractSecurityConfig implements ISecurityConfig {
  /**
   * {@inheritDoc}
   */
  @Bean
  @Override
  public @NotNull SecurityFilterChain securityFilterChain(@NotNull final IWebFilter webFilter,
                                                          @NotNull final HttpSecurity http) throws Exception {
    log.info("Initialized security filter of spring module...");

    return http
      .httpBasic(Customizer.withDefaults())
      .authorizeHttpRequests(authorize -> authorize
        .anyRequest()
        .permitAll())
      .addFilterBefore(webFilter, UsernamePasswordAuthenticationFilter.class)
      .csrf(AbstractHttpConfigurer::disable)
      .build();
  }

  /**
   * {@inheritDoc}
   */
  @Bean
  public @NotNull UserDetailsService userDetailsService() {
    final UserDetailsService userDetailsService = username -> {
      throw new UsernameNotFoundException("No users are managed, this function is deactivated.");
    };

    log.info("The service to block the UserDetailsService was successfully created.");
    return userDetailsService;
  }

  /**
   * {@inheritDoc}
   */
  @Bean
  public @NotNull PrePostTemplateDefaults prePostTemplateDefaults() {
    return new PrePostTemplateDefaults();
  }
}
