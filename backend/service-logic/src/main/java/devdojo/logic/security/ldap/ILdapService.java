package devdojo.logic.security.ldap;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * diese klasse exisitert nur als workaround
 */

public interface ILdapService {

  Map<String, Object> loadUserAttributes(@NotBlank(message = "UID must not be empty") final String uid);

}
