package devdojo.logic.security.ldap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractLdapConfig implements ILdapConfig {

  private final String ldapUrl;
  private final String ldapBase;
  private final String ldapUserPattern;

  protected AbstractLdapConfig(
    @NotBlank(message = "LDAP URL must not be empty") final String ldapUrl,
    @NotBlank(message = "LDAP Base must not be empty") final String ldapBase,
    @NotBlank(message = "LDAP user DN pattern must not be empty") final String ldapUserPattern
  ) {
    this.ldapUrl = ldapUrl;
    this.ldapBase = ldapBase;
    this.ldapUserPattern = ldapUserPattern;
  }

  @Bean
  @Override
  public LdapContextSource ldapContextSource() {
    final LdapContextSource contextSource = new LdapContextSource();
    contextSource.setUrl(this.ldapUrl);
    contextSource.setBase(this.ldapBase);
    contextSource.afterPropertiesSet();
    return contextSource;
  }

  @Bean
  @Override
  public LdapTemplate ldapTemplate(final LdapContextSource contextSource) {
    return new LdapTemplate(contextSource);
  }

  @Bean
  @Override
  public AuthenticationProvider ldapAuthenticationProvider() {
    final BindAuthenticator authenticator = new BindAuthenticator(ldapContextSource());
    authenticator.setUserDnPatterns(new String[] {this.ldapUserPattern});

    return new LdapAuthenticationProvider(authenticator, new NullLdapAuthoritiesPopulator());
  }
}
