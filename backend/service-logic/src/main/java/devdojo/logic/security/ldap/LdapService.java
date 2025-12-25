package devdojo.logic.security.ldap;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

@Service
public class LdapService extends AbstractLdapService {
  public LdapService(final LdapTemplate ldapTemplate) {
    super(ldapTemplate);
  }
}
