package devdojo.logic.security.ldap;

import jakarta.validation.constraints.NotNull;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;

public interface ILdapConfig {

  LdapContextSource ldapContextSource();

  LdapTemplate ldapTemplate(
    @NotNull(message = "LdapContextSource darf nicht null sein") final LdapContextSource contextSource);

  AuthenticationProvider ldapAuthenticationProvider();
}
