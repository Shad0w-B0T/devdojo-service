package devdojo.logic.security.ldap;

import java.util.Map;

/**
 * diese klasse exisitert nur als workaround
 */

public interface ILdapService {

  public Map<String, Object> loadUserAttributes(final String uid);

}
