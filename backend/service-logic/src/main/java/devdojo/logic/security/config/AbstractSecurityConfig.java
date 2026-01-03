package devdojo.logic.security.config;

import devdojo.logic.security.web.IWebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.method.PrePostTemplateDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public abstract class AbstractSecurityConfig implements ISecurityConfig {
  /**
   * {@inheritDoc}
   */
  @Bean
  @Override
  public @NotNull SecurityFilterChain securityFilterChain(
    @NotNull final AuthenticationProvider provider,
    @NotNull final IWebFilter webFilter,
    @NotNull final HttpSecurity http
  ) throws Exception {

    log.info("Initialized security filter of spring module...");

    return http
      .authenticationProvider(provider)

      .formLogin(form -> form
        .loginProcessingUrl("/api/login")
        .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
        .failureHandler((req, res, ex) -> {
          System.out.println(ex.getMessage());
          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        })
        .permitAll())

      .exceptionHandling(e -> e
        .authenticationEntryPoint((req, res, ex) ->
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

      .logout(logout -> logout
        .logoutUrl("/api/logout")
        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)))

      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/login", "/api/logout").permitAll()
        .anyRequest().authenticated())

      .addFilterAfter(webFilter, UsernamePasswordAuthenticationFilter.class)
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
