package devdojo.logic.security.config;

import devdojo.logic.security.web.IWebFilter;
import devdojo.logic.security.web.AbstractJwtAuthenticationFilter;
import devdojo.logic.security.web.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.method.PrePostTemplateDefaults;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public abstract class AbstractSecurityConfig implements ISecurityConfig {

  @Bean
  public AuthenticationManager authenticationManager(final AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  /**
   * {@inheritDoc}
   */
  @Bean
  @Override
  public @NotNull SecurityFilterChain securityFilterChain(
    @NotNull final AuthenticationProvider provider,
    @NotNull @Qualifier("auth") final IWebFilter authFilter,
    @NotNull @Qualifier("jwt") final IWebFilter jwtFilter,
    @NotNull final HttpSecurity http
  ) throws Exception {

    log.info("Initialized security filter of spring module...");

    return http
      .authenticationProvider(provider)
      .sessionManagement(s ->
        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(e ->
        e.authenticationEntryPoint(
          (req, res, ex) ->
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/login", "/api/refresh",  "/ws/**").permitAll()
        .anyRequest().authenticated())
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .addFilterAfter(authFilter, JwtAuthenticationFilter.class)
      .csrf(AbstractHttpConfigurer::disable)
      .build();
  }

  /**
   * {@inheritDoc}
   */
  @Bean
  public @NotNull PrePostTemplateDefaults prePostTemplateDefaults() {
    return new PrePostTemplateDefaults();
  }
}
