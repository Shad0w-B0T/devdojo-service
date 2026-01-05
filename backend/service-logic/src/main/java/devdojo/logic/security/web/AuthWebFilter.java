package devdojo.logic.security.web;

import devdojo.logic.security.ldap.ILdapService;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service("auth")
public class AuthWebFilter extends AbstractAuthWebFilter {

  protected AuthWebFilter(final ILdapService ldapService) {
    super(ldapService);
  }
}
