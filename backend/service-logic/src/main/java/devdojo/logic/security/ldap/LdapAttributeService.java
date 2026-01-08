package devdojo.logic.security.ldap;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

@Service
public class LdapAttributeService extends AbstractLdapAttributeService {

  protected LdapAttributeService(final LdapTemplate ldapTemplate) {
    super(ldapTemplate);
  }
}
