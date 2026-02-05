package devdojo.logic.security.ldap.service;

import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class LdapService extends AbstractLdapService {

  protected LdapService(final IUserService userService,
                        final ILdapAttributeService ldapAttributeService) {
    super(userService, ldapAttributeService);
  }
}
