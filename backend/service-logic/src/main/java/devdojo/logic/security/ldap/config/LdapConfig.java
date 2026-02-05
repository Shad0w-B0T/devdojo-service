package devdojo.logic.security.ldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdapConfig extends AbstractLdapConfig {

  protected LdapConfig(@Value("${spring.ldap.urls}") final String ldapUrl,
                       @Value("${spring.ldap.base}") final String ldapBase,
                       @Value("${spring.security.ldap.user-dn-patterns}") final String ldapUserPattern) {
    super(ldapUrl, ldapBase, ldapUserPattern);
  }
}
