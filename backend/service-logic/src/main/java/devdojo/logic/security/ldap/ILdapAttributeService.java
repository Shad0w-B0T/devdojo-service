package devdojo.logic.security.ldap;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public interface ILdapAttributeService {

  Map<String, Object> loadUserAttributes(@NotBlank(message = "UID must not be empty") final String uid);

}
