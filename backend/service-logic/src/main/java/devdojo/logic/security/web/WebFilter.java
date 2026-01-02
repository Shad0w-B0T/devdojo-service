package devdojo.logic.security.web;

import devdojo.logic.security.ldap.ILdapService;
import devdojo.logic.user.IUserService;
import org.springframework.stereotype.Service;

@Service
public class WebFilter extends AbstractWebFilter {

  protected WebFilter(final IUserService userService,
                      final ILdapService ldapService) {
    super(userService, ldapService);
  }
}
