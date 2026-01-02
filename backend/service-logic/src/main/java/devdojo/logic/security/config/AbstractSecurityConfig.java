package devdojo.logic.security.config;

import devdojo.logic.security.web.IWebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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
    @NotNull AuthenticationProvider provider,
    @NotNull IWebFilter webFilter,
    @NotNull HttpSecurity http
  ) throws Exception {

    log.info("Initialized security filter of spring module...");

    return http
      .authenticationProvider(provider)

      .formLogin(form -> form
        .loginProcessingUrl("/login")
        .successHandler((req, res, auth) -> res.setStatus(200))
        .failureHandler((req, res, ex) -> res.setStatus(401))
        .permitAll())

      .exceptionHandling(e -> e
        .authenticationEntryPoint((req, res, ex) ->
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200)))

      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/login", "/logout").permitAll()
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
