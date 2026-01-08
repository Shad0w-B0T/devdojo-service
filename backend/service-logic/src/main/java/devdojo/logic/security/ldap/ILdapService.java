package devdojo.logic.security.ldap;

import devdojo.logic.database.user.UserEntity;
import devdojo.logic.user.IUserService;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Objects;

/**
 * diese klasse exisitert nur als workaround
 */

public interface ILdapService {

  UserEntity loadOrSyncUser(String authId);

}
