package devdojo.logic.security.ldap.service;

import devdojo.logic.database.user.UserEntity;

/**
 * diese klasse exisitert nur als workaround
 */

public interface ILdapService {

  UserEntity loadOrSyncUser(String authId);

}
